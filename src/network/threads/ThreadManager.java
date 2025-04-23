package network.threads;

import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import io.IoManager;
import messages.foreign.ForeignMessage;
import messages.internal.InternalMessage;
import utils.Exceptions.ThreadNotStartedException;

public class ThreadManager {
    private final DatagramSocket SOCKET;
    private final ConcurrentHashMap<NetworkNode, Integer> NODES; // node -> seconds since last message
    
    private LinkedList<Thread> threads;

    public ThreadManager(DatagramSocket socket, ConcurrentHashMap<NetworkNode, Integer> nodes) {
        this.SOCKET  = socket;
        this.NODES   = nodes;
        this.threads = new LinkedList<Thread>();
    }

    public BlockingQueue<ForeignMessage> createSender() {
        BlockingQueue<ForeignMessage> queue  = new LinkedBlockingDeque<ForeignMessage>();
        SenderThread                  thread = new SenderThread(SOCKET, queue);

        threads.add(new Thread(() -> thread.run()));
        return queue;
    }

    public BlockingQueue<InternalMessage> createReceiver() {
        BlockingQueue<InternalMessage> queue  = new LinkedBlockingDeque<InternalMessage>();
        ReceiverThread                 thread = new ReceiverThread(SOCKET, queue);

        threads.add(new Thread(() -> thread.run()));
        return queue;
    }

    public BlockingQueue<InternalMessage> createIO(BlockingQueue<InternalMessage> sender) {
        BlockingQueue<InternalMessage> receiver = new LinkedBlockingDeque<InternalMessage>();
        IoManager                      thread   = new IoManager(receiver, sender, NODES);

        threads.add(new Thread(() -> thread.run()));
        return receiver;
    }

    public void startThreads() {
        threads.stream()
               .filter(x -> x == null)
               .findFirst()
               .ifPresent(x -> { throw new ThreadNotStartedException("Thread object not created"); });
    
        threads.forEach(Thread::start);
    }

    public void stopThreads() {
        threads.forEach(Thread::interrupt);
        threads.clear();
    }
}
