package network.messageHandlers;

import static utils.Constants.Configs.NODE_TIMEOUT_SEC;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import interfaces.visitors.foreign.ForeignVisitor;
import interfaces.visitors.internal.InternalReceivedMessageVisitor;
import interfaces.visitors.internal.InternalRequestMessageVisitor;
import messages.ThreadMessage;
import messages.foreign.ForeignFileMessage;
import messages.foreign.ForeignMessage;
import messages.internal.InternalMessage;
import messages.internal.received.*;
import messages.internal.requested.*;
import messages.internal.requested.send.InternalRequestSendAckMessage;
import messages.internal.requested.send.InternalRequestSendFileMessage;
import messages.internal.requested.send.InternalRequestSendNAckMessage;
import messages.internal.requested.send.InternalRequestSendTalkMessage;
import utils.Exceptions.EndExecutionException;


public class MessageHandler implements InternalReceivedMessageVisitor,
                                        InternalRequestMessageVisitor,
                                        ForeignVisitor {
    private static volatile int idCounter = 0;

    private BlockingQueue<ForeignMessage> udpSenderQueue;
    private BlockingQueue<InternalMessage> loggerQueue;
    private ConcurrentHashMap<InetAddress, Integer> activeNodes; // node -> seconds since last message

    private HashMap<Integer, ForeignMessage> sentMessages;                  // messageId -> message
    private HashMap<Integer, InternalRequestSendFileMessage> pendingFiles;  // messageId -> message

    public MessageHandler(
        BlockingQueue<ForeignMessage> udpSenderQueue,
        BlockingQueue<InternalMessage> loggerQueue,
        ConcurrentHashMap<InetAddress, Integer> activeNodes
    ) {
        this.udpSenderQueue = udpSenderQueue;
        this.loggerQueue    = loggerQueue;
        this.activeNodes    = activeNodes;

        sentMessages = new HashMap<>();
        pendingFiles = new HashMap<>();
    }

    /**
     * Registers the Ip Node to the Network Map, if it's not already registered
     * Resets the timeout timer, if it is
     * @param ip is the InetAddress to have it's timer reset
     */
    private void registerNode(InetAddress ip) {
        activeNodes.compute(ip, (unusedKey, unusedValue) -> NODE_TIMEOUT_SEC);
    }

    private void sendMessage(int messageId, ForeignMessage message) {
        sentMessages.putIfAbsent(messageId, message);
        udpSenderQueue.offer(message);
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalReceivedMessageVisitor

    @Override
    public void visit(InternalReceivedHeartbeatMessage message) {
        registerNode(message.getSourceIp());
        loggerQueue.offer(message);
    }

    @Override
    public void visit(InternalReceivedTalkMessage message) {
        registerNode(message.getSourceIp());
        loggerQueue.offer(message);
    }

    @Override
    public void visit(InternalReceivedFileMessage message) {
        registerNode(message.getSourceIp());
        loggerQueue.offer(message);
    }

    @Override
    public void visit(InternalReceivedChunkMessage message) {
        registerNode(message.getSourceIp());
        loggerQueue.offer(message);
    }

    @Override
    public void visit(InternalReceivedEndMessage message) {
        registerNode(message.getSourceIp());
        loggerQueue.offer(message);
    }

    @Override
    public void visit(InternalReceivedAckMessage message) {
        ForeignMessage sentMessage;
        registerNode(message.getSourceIp());
        loggerQueue.offer(message);

        sentMessage = sentMessages.remove(message.getAcknowledgedId());
        if (sentMessage != null) sentMessage.accept(this);
    }

    @Override
    public void visit(InternalReceivedNAckMessage message) {
        registerNode(message.getSourceIp());
        loggerQueue.offer(message);

        sentMessages.remove(message.getNonAcknowledgedId());
        pendingFiles.remove(message.getNonAcknowledgedId());
    }

    @Override
    public void visit(InternalReceivedUnsupportedMessage message) {
        registerNode(message.getSourceIp());
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

        loggerQueue.offer(message);

        sentMessage = sentMessages.remove(message.getResendId());
        if (sentMessage == null) {
            return;
        }

        messageId   = idCounter++;
        sentMessages.put(messageId, sentMessage);
    }

    @Override
    public void visit(InternalRequestSendTalkMessage request) {
        int messageId = idCounter++;
        sendMessage(messageId,
            ThreadMessage.foreignMessage(messageId)
                            .talk(request.getContent())
                            .to(request.getDestinationIp())
        );
    }

    @Override
    public void visit(InternalRequestSendFileMessage request) {
        int messageId = idCounter++;
        pendingFiles.put(messageId, request);
        sendMessage(messageId,
            ThreadMessage.foreignMessage(messageId)
                            .file(request.getFileName())
                            .fileSize(request.getFileSize())
                            .to(request.getDestinationIp())
        );
    }

    @Override
    public void visit(InternalRequestSendAckMessage request) {
        udpSenderQueue.offer(
            ThreadMessage.foreignMessage()
                            .ack(request.getAcknowledgedMessageId())
                            .to(request.getDestinationIp())
        );
    }

    @Override
    public void visit(InternalRequestSendNAckMessage request) {
        udpSenderQueue.offer(
            ThreadMessage.foreignMessage()
                            .nAck(request.getNonAcknowledgedMessageId())
                            .because(request.getReason())
                            .to(request.getDestinationIp())
        );
    }

    // ****************************************************************************************************
    // ForeignVisitor interface implementation

    @Override
    public void ack(ForeignMessage request) {
        return;
    }

    @Override
    public void ack(ForeignFileMessage request) {
        InternalRequestSendFileMessage sentMessage;
        byte[][] fileContent;

        sentMessage = pendingFiles.remove(request.getMessageId());
        fileContent = sentMessage.getSplitData();

        for (int i = 0; i < fileContent.length; i++) {
            int messageId = idCounter++;
            sendMessage(messageId,
                ThreadMessage.foreignMessage(messageId)
                                .chunk(i)
                                .data(fileContent[i])
                                .to(request.getDestinationIp())
            );
        }
    }
}
