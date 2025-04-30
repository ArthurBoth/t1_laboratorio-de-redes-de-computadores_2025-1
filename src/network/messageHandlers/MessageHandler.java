package network.messageHandlers;


import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import interfaces.visitors.foreign.ForeignVisitor;
import interfaces.visitors.internal.InternalReceivedMessageVisitor;
import interfaces.visitors.internal.InternalRequestMessageVisitor;
import messages.ThreadMessage;
import messages.foreign.ForeignChunkMessage;
import messages.foreign.ForeignEndMessage;
import messages.foreign.ForeignFileMessage;
import messages.foreign.ForeignMessage;
import messages.internal.InternalMessage;
import messages.internal.received.*;
import messages.internal.requested.*;
import messages.internal.requested.send.InternalRequestSendAckMessage;
import messages.internal.requested.send.InternalRequestSendChunkMessage;
import messages.internal.requested.send.InternalRequestSendEndMessage;
import messages.internal.requested.send.InternalRequestSendFileMessage;
import messages.internal.requested.send.InternalRequestSendFullFileMessage;
import messages.internal.requested.send.InternalRequestSendNAckMessage;
import messages.internal.requested.send.InternalRequestSendTalkMessage;
import network.NetworkNode;
import utils.Constants;
import utils.Exceptions.EndExecutionException;


public class MessageHandler implements InternalReceivedMessageVisitor,
                                        InternalRequestMessageVisitor,
                                        ForeignVisitor {
    private static volatile int idCounter = 0;

    private BlockingQueue<ForeignMessage> udpSenderQueue;
    private BlockingQueue<InternalMessage> loggerQueue;
    private ConcurrentHashMap<InetAddress, NetworkNode> activeNodes; // ip        -> node
    private ConcurrentHashMap<Integer, Integer> messagesMap;         // messageId -> seconds since sent

    private HashMap<Integer, ForeignMessage> sentMessages;  // messageId -> message

    public MessageHandler(
        BlockingQueue<ForeignMessage> udpSenderQueue,
        BlockingQueue<InternalMessage> loggerQueue,
        ConcurrentHashMap<InetAddress, NetworkNode> activeNodes,
        ConcurrentHashMap<Integer, Integer> messagesMap
    ) {
        this.udpSenderQueue = udpSenderQueue;
        this.loggerQueue    = loggerQueue;
        this.activeNodes    = activeNodes;
        this.messagesMap    = messagesMap;

        sentMessages = new HashMap<>();
    }

    /**
     * Registers the Ip Node to the Network Map, if it's not already registered
     * Resets the timeout timer, if it is
     * @param ip is the InetAddress to have it's timer reset
     * @param port is the port of the node
     * @param name is the name of the node
     * @see NetworkNode
     * @see NetworkNode#resetHeartbeat()
     */
    private void registerNode(InetAddress ip, int port, String name) {
        activeNodes.computeIfPresent(
            ip, (key, node) -> (node.ARTIFICIAL_NODE) ? NetworkNode.of(ip, port, name) : node 
        );
        activeNodes.putIfAbsent(ip, NetworkNode.of(ip, port, name));
        activeNodes.get(ip).resetHeartbeat();
    }

    private void sendMessage(int messageId, ForeignMessage message) {
        sentMessages.putIfAbsent(messageId, message);
        udpSenderQueue.offer(message);
        messagesMap.put(messageId, Constants.Configs.MESSAGE_ACK_TIMEOUT_SEC);
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalReceivedMessageVisitor

    @Override
    public void visit(InternalReceivedHeartbeatMessage message) {
        registerNode(message.getSourceIp(), message.getPort(), message.getName());
        loggerQueue.offer(message);
    }

    @Override
    public void visit(InternalReceivedTalkMessage message) {
        loggerQueue.offer(message);
    }

    @Override
    public void visit(InternalReceivedFileMessage message) {
        loggerQueue.offer(message);
    }

    @Override
    public void visit(InternalReceivedChunkMessage message) {
        loggerQueue.offer(message);
    }

    @Override
    public void visit(InternalReceivedEndMessage message) {
        loggerQueue.offer(message);
    }

    @Override
    public void visit(InternalReceivedAckMessage message) {
        ForeignMessage sentMessage;
        loggerQueue.offer(message);

        messagesMap.remove(message.getAcknowledgedId());
        sentMessage = sentMessages.remove(message.getAcknowledgedId());
        if (sentMessage != null) sentMessage.ackcept(this);
    }

    @Override
    public void visit(InternalReceivedNAckMessage message) {
        ForeignMessage sentMessage;
        loggerQueue.offer(message);

        messagesMap.remove(message.getNonAcknowledgedId());
        sentMessage = sentMessages.remove(message.getNonAcknowledgedId());
        if (sentMessage != null) sentMessage.nackcept(this);
    }

    @Override
    public void visit(InternalReceivedUnsupportedMessage message) {
        loggerQueue.offer(message);
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalRequestMessageVisitor

    @Override
    public void visit(InternalRequestExitMessage request) {
        throw new EndExecutionException();
    }

    /**
     * Resends the message with the given id
     * <br></br>
     * Internally, the idCounter is incremented, yet the udp packet sent, holds the
     * same messageId as the original message
     * @param message keeps the messageId of the message to be resent
     */
    @Override
    public void visit(InternalRequestResendMessage message) {
        int messageId;
        ForeignMessage sentMessage;

        messageId   = message.getResendId();
        sentMessage = sentMessages.remove(messageId);
        if (sentMessage == null) {
            return;
        }

        loggerQueue.offer(message);
        sendMessage(messageId, sentMessage);
    }

    @Override
    public void visit(InternalRequestSendTalkMessage request) {
        int messageId = idCounter++;
        sendMessage(messageId,
            ThreadMessage.foreignMessage(messageId)
                            .talk(request.getContent())
                            .to(request.getDestinationIp())
                            .at(request.getPort())
        );
    }

    @Override
    public void visit(InternalRequestSendFileMessage request) {
        int messageId = idCounter++;
        sendMessage(messageId,
            ThreadMessage.foreignMessage(messageId)
                            .file(request.getFileName())
                            .fileSize(request.getFileSize())
                            .to(request.getDestinationIp())
                            .at(request.getPort())
        );
    }

    @Override
    public void visit(InternalRequestSendChunkMessage request) {
        int messageId = idCounter++;
        sendMessage(messageId,
            ThreadMessage.foreignMessage(messageId)
                            .chunk(request.getSequenceNumber())
                            .data(request.getChunk())
                            .to(request.getDestinationIp())
                            .at(request.getPort())
        );
    }

    @Override
    public void visit(InternalRequestSendEndMessage request) {
        int messageId = idCounter++;
        sendMessage(messageId,
            ThreadMessage.foreignMessage(messageId)
                            .end(request.getHash())
                            .to(request.getDestinationIp())
                            .at(request.getPort())
        );
    }

    @Override
    public void visit(InternalRequestSendAckMessage request) {
        udpSenderQueue.offer(
            ThreadMessage.foreignMessage()
                            .ack(request.getAcknowledgedMessageId())
                            .to(request.getDestinationIp())
                            .at(request.getPort())
        );
    }

    @Override
    public void visit(InternalRequestSendNAckMessage request) {
        udpSenderQueue.offer(
            ThreadMessage.foreignMessage()
                            .nAck(request.getNonAcknowledgedMessageId())
                            .because(request.getReason())
                            .to(request.getDestinationIp())
                            .at(request.getPort())
        );
    }

    // ****************************************************************************************************
    // ForeignVisitor interface implementation

    @Override
    public void ack(ForeignMessage message) {
        return;
    }

    @Override
    public void ack(ForeignFileMessage message) {
        loggerQueue.offer(
            ThreadMessage.internalMessage(getClass())
                .request()
                .send()
                .fullFile(message.getFileName())
                .to(message.getDestinationIp())
                .at(message.getPort())
        );
    }

    @Override
    public void ack(ForeignChunkMessage message) {
        loggerQueue.offer(
            ThreadMessage.internalMessage(getClass())
                .request()
                .updateSendStatus(message.getData().length)
        );
    }

    @Override
    public void ack(ForeignEndMessage message) {
        loggerQueue.offer(
            ThreadMessage.internalMessage(getClass())
                .request()
                .displaySuccess()
        );
    }

    @Override
    public void nack(ForeignMessage message) {
        return;
    }

    @Override
    public void nack(ForeignFileMessage message) {
        loggerQueue.offer(
            ThreadMessage.internalMessage(getClass())
                .request()
                .abortFileSending()
        );
    }

    @Override
    public void nack(ForeignEndMessage message) {
        loggerQueue.offer(
            ThreadMessage.internalMessage(getClass())
                .request()
                .displayFailure()
        );
    }

    // ****************************************************************************************************
    // IllegalStateException
    // These methods should never be called, as these messages should be handled elsewhere

    @Override
    public void visit(InternalRequestSendFullFileMessage message) {
        throw new IllegalStateException("RequestFullFileMessage should not arrive in the message handler");
    }

    @Override
    public void visit(InternalRequestUpdateSendStatusMessage message) {
        throw new IllegalStateException("UpdateSendStatusMessage should not arrive in the message handler");
    }

    @Override
    public void visit(InternalRequestDisplaySuccessMessage message) {
        throw new IllegalStateException("DisplaySuccessMessage should not arrive in the message handler");
    }

    @Override
    public void visit(InternalRequestDisplayFailureMessage message) {
        throw new IllegalStateException("DisplayFailureMessage should not arrive in the message handler");
    }

    @Override
    public void visit(InternalRequestAbortFileSendingMessage message) {
        throw new IllegalStateException("AbortFileSendingMessage should not arrive in the message handler");
    }
}
