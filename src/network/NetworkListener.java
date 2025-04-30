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
    private BlockingQueue<InternalMessage> ioReceiverQueue;
    private BlockingQueue<InternalMessage> ioSenderQueue;
    private BlockingQueue<InternalMessage> udpReceiverQueue;
    private BlockingQueue<ForeignMessage> udpSenderQueue;
    private BlockingQueue<InternalMessage> timerReceiverQueue;

    ConcurrentHashMap<InetAddress, NetworkNode> activeNodes; // ip -> node

    MessageHandler handler;

    public NetworkListener(ConcurrentHashMap<InetAddress, NetworkNode> activeNodes) {
        this.activeNodes = activeNodes;
    }

    public void setup(ConcurrentHashMap<Integer, Integer> messagesMap) {
        handler = new MessageHandler(udpSenderQueue, ioSenderQueue, activeNodes, messagesMap);
    }

    public void startListening() {
        InternalMessage message;

        try {
            while (true) {
                message = listen();
                message.accept(this);
            }
        } catch (EndExecutionException e) {
            throw e;
        } catch (Exception e) {
            ConsoleLogger.logError(e);
        }
    }

    private InternalMessage listen() {
        InternalMessage message;
        while(true) {
            message = ioReceiverQueue.poll();
            if (message != null) return message;
            message = udpReceiverQueue.poll();
            if (message != null) return message;
            message = timerReceiverQueue.poll();
            if (message != null) return message;
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

    public void setTimerReceiverQueue(BlockingQueue<InternalMessage> timerReceiverQueue) {
        this.timerReceiverQueue = timerReceiverQueue;
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
