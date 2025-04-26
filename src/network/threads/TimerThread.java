package network.threads;

import static utils.Constants.Configs.HEARTBEAT_INTERVAL_SEC;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import messages.ThreadMessage;
import messages.foreign.ForeignMessage;
import messages.internal.InternalMessage;

public class TimerThread extends AppThread {
    private HashMap<Integer, Integer> messagesWaitingAck;         // messageId -> seconds since sent
    private ConcurrentHashMap<InetAddress, Integer> activeNodes;  // node      -> seconds since last message

    private BlockingQueue<InternalMessage> handlerSenderQueue;  // queue to receive messages from other threads
    private BlockingQueue<ForeignMessage> udpSenderQueue;       // queue to receive messages from other threads

    private int seconds = 0;

    public TimerThread(
        BlockingQueue<InternalMessage> handlerSenderQueue,
        BlockingQueue<ForeignMessage> udpSenderQueue,
        ConcurrentHashMap<InetAddress, Integer> activeNodes
    ) {
        this.udpSenderQueue     = udpSenderQueue;
        this.handlerSenderQueue = handlerSenderQueue;
        this.activeNodes        = activeNodes;

        this.messagesWaitingAck = new HashMap<>();
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

        
        activeNodes.replaceAll((x, seconds) -> seconds - 1);
        messagesWaitingAck.replaceAll((x, seconds) -> seconds - 1);
    }

    private void checkMaps() {
        activeNodes.forEach((node, seconds) -> {
            if (seconds <= 0 ) {
                activeNodes.remove(node);
            }
        });

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
