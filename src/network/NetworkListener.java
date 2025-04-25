package network;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import interfaces.visitors.internal.InternalMessageVisitor;
import messages.foreign.ForeignMessage;
import messages.internal.InternalMessage;
import messages.internal.received.InternalReceivedMessage;
import messages.internal.requested.InternalRequestMessage;
import network.messageHandlers.MessageHandler;
import utils.ConsoleLogger;
import utils.Exceptions.EndExecutionException;

public class NetworkListener implements InternalMessageVisitor {
    BlockingQueue<InternalMessage> ioReceiverQueue;
    BlockingQueue<InternalMessage> ioSenderQueue;
    BlockingQueue<InternalMessage> udpReceiverQueue;
    BlockingQueue<ForeignMessage> udpSenderQueue;

    ConcurrentHashMap<InetAddress, Integer> activeNodes; // node -> seconds since last message

    MessageHandler handler;

    public NetworkListener(ConcurrentHashMap<InetAddress, Integer> activeNodes) {
        this.activeNodes = activeNodes;
    }

    private void setup() {
        handler = new MessageHandler(udpSenderQueue, ioSenderQueue, activeNodes);
    }

    public void startListening() {
        InternalMessage message;
        setup();

        try {
            while (true) {
                message = ioReceiverQueue.poll();
                if (message != null) message.accept(this);
                message = udpReceiverQueue.poll();
                if (message != null) message.accept(this);
            }
        } catch (EndExecutionException e) {
            return;
        } catch (Exception e) {
            ConsoleLogger.logError(e);
        }
    }

    public void setIoReceiverQueue(BlockingQueue<InternalMessage> ioReceiverQueue) {
        this.ioReceiverQueue = ioReceiverQueue;
    }

    public void setIoSenderQueue(BlockingQueue<InternalMessage> ioSenderQueue) {
        this.ioSenderQueue = ioSenderQueue;
    }

    public void setUdpReceiverQueue(BlockingQueue<InternalMessage> udpReceiverQueue) {
        this.udpReceiverQueue = udpReceiverQueue;
    }

    public void setUdpSenderQueue(BlockingQueue<ForeignMessage> udpSenderQueue) {
        this.udpSenderQueue = udpSenderQueue;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalMessageVisitor

    @Override
    public void visit(InternalRequestMessage message) {
        message.accept(handler);
    }

    @Override
    public void visit(InternalReceivedMessage message) {
        message.accept(handler);
    }
}
