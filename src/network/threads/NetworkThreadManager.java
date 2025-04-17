package network.threads;

import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import constants.Constants;
import io.IOManager;
import network.messages.ThreadMessage;

public class NetworkThreadManager {
    // Threads
    private ReceiverThread receiver;
    private SenderThread sender;
    private IOManager io;

    public NetworkThreadManager(DatagramSocket socket,
                                BlockingQueue<ThreadMessage> sendMessages, 
                                BlockingQueue<ThreadMessage> receiveMessages,
                                ConcurrentHashMap<NetworkNode, Integer> activeNodes) {
        receiver = new ReceiverThread(socket, receiveMessages);
        sender   = new SenderThread(socket, sendMessages);
        io       = new IOManager(receiveMessages, activeNodes);
        // timer =  new TimerThread(socket, sendMessages, activeNodes);
    }

    public void startThreads() {
        new Thread(() -> receiver.run()).start();
        new Thread(() -> sender.run()).start();
        new Thread(() -> io.run()).start();
        // new Thread(() -> timer.run()).start();
    }

    public void stopThreads() {
        Thread senderThread   = sender.stop();
        Thread receiverThread = receiver.stop();
        // Thread timerThread = timer.stop();
        
        // IO thread is the one notifying the EXIT messages, it will be the first to stop
        try {
            senderThread.join(Constants.Configs.THREAD_TIMEOUT_MS);
            receiverThread.join(Constants.Configs.THREAD_TIMEOUT_MS);
            // timerThread.join(Constants.Configs.THREAD_TIMEOUT_MS);
        } catch (InterruptedException e) {
            return;
        } finally {
            senderThread.interrupt();
            receiverThread.interrupt();
            // timerThread.interrupt();
        }
    }
}
