package network.threads;

import static utils.Constants.Configs.HEARTBEAT_INTERVAL_SEC;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import messages.ThreadMessage;
import messages.foreign.ForeignMessage;
import messages.internal.InternalMessage;
import network.NetworkNode;

public class TimerThread extends AppThread {
    private ConcurrentHashMap<Integer, Integer> messagesWaitingAck;   // messageId -> seconds since sent
    private ConcurrentHashMap<InetAddress, NetworkNode> activeNodes;  // ip        -> node

    private BlockingQueue<InternalMessage> handlerSenderQueue;
    private BlockingQueue<ForeignMessage> udpSenderQueue;

    private int seconds = 0;

    public TimerThread(
        BlockingQueue<InternalMessage> handlerSenderQueue,
        BlockingQueue<ForeignMessage> udpSenderQueue,
        ConcurrentHashMap<InetAddress, NetworkNode> activeNodes
    ) {
        this.udpSenderQueue     = udpSenderQueue;
        this.handlerSenderQueue = handlerSenderQueue;
        this.activeNodes        = activeNodes;

        this.messagesWaitingAck = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<Integer, Integer> getMessagesMap() {
        return messagesWaitingAck;
    }

    @Override
    public void run() {
        while (running) {
            clockTick();
            checkMaps();
            heartbeat();
        }
    }
    
    private void clockTick() {
        final int ONE_SECOND = 1;

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(ONE_SECOND));
        } catch (InterruptedException e) {
            running = false;
            return;
        }
        
        activeNodes.forEach((ip, node) -> {
            if (node.tickHeartbeat())
                activeNodes.remove(ip);
        });
        messagesWaitingAck.replaceAll((x, seconds) -> seconds - 1);
    }

    private void checkMaps() {
        messagesWaitingAck.forEach((messageId, seconds) -> {
            if (seconds <= 0) {
                messagesWaitingAck.remove(messageId);
                handlerSenderQueue.offer(
                    ThreadMessage.internalMessage(getClass())
                        .request()
                        .resend(messageId)
                );
            }
        });
    }

    private void heartbeat() {
        if (seconds == 0) {
            udpSenderQueue.offer(
                ThreadMessage.foreignMessage()
                    .heartbeat()
            );
        }

        seconds = (seconds + 1) % HEARTBEAT_INTERVAL_SEC;
    }
}
