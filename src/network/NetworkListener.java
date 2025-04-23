package network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import io.consoleIO.ConsoleLogger;
import messages.ThreadMessage;
import messages.foreign.ForeignMessage;
import messages.internal.InternalMessage;
import network.messageHandlers.MessageHandler;
import network.threads.NetworkNode;
import utils.Exceptions.EndExecutionException;

public class NetworkListener {
    BlockingQueue<ThreadMessage> ioReceiverQueue;
    BlockingQueue<ThreadMessage> ioSenderQueue;
    BlockingQueue<InternalMessage> udpReceiverQueue;
    BlockingQueue<ForeignMessage> udpSenderQueue;

    ConcurrentHashMap<NetworkNode, Integer> activeNodes;

    MessageHandler handler;

    public NetworkListener() {
    }

    private void setup() {
        handler = new MessageHandler(udpSenderQueue, ioSenderQueue);
    }

    public void startListening() {
        ThreadMessage message;
        setup();

        try {
            while (true) {
                message = ioReceiverQueue.poll();
                if (message != null) message.accept(handler);
                message = udpReceiverQueue.poll();
                if (message != null) message.accept(handler);
            }
        } catch (EndExecutionException e) {
            return;
        } catch (Exception e) {
            ConsoleLogger.logError(e);
        }
    }

    public void setIoReceiverQueue(BlockingQueue<ThreadMessage> ioReceiverQueue) {
        this.ioReceiverQueue = ioReceiverQueue;
    }

    public void setIoSenderQueue(BlockingQueue<ThreadMessage> ioSenderQueue) {
        this.ioSenderQueue = ioSenderQueue;
    }

    public void setUdpReceiverQueue(BlockingQueue<InternalMessage> udpReceiverQueue) {
        this.udpReceiverQueue = udpReceiverQueue;
    }

    public void setUdpSenderQueue(BlockingQueue<ForeignMessage> udpSenderQueue) {
        this.udpSenderQueue = udpSenderQueue;
    }
}
