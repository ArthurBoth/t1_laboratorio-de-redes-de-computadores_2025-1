package network;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import messages.ThreadMessage;
import messages.foreign.ForeignMessage;
import messages.internal.InternalMessage;
import network.threads.NetworkNode;
import network.threads.ThreadManager;
import utils.Constants;

public class NetworkManager {
    private DatagramSocket socket;

    private NetworkListener listener;
    private ThreadManager threadManager;
    private ConcurrentHashMap<NetworkNode, Integer> activeNodes; // node -> seconds since last message

    public void start() throws SocketException {
        setup();
        listener.startListening();
        threadManager.stopThreads();
        socket.close();
    }

    private void setup() throws SocketException {
        BlockingQueue<ThreadMessage> ioSenderQueue;
        BlockingQueue<ThreadMessage> ioReceiverQueue;
        BlockingQueue<InternalMessage> udpReceiverQueue;
        BlockingQueue<ForeignMessage> udpSenderQueue;

        socket        = new DatagramSocket(Constants.Configs.DEFAULT_PORT);
        activeNodes   = new ConcurrentHashMap<NetworkNode, Integer>();
        listener      = new NetworkListener();
        threadManager = new ThreadManager(socket, activeNodes);

        udpSenderQueue   = threadManager.createSender();
        udpReceiverQueue = threadManager.createReceiver();
        ioSenderQueue    = new LinkedBlockingQueue<ThreadMessage>();
        ioReceiverQueue  = threadManager.createIO(ioSenderQueue);

        listener.setIoReceiverQueue(ioReceiverQueue);
        listener.setIoSenderQueue(ioSenderQueue);
        listener.setUdpReceiverQueue(udpReceiverQueue);
        listener.setUdpSenderQueue(udpSenderQueue);
        
        socket.setBroadcast(true);
        threadManager.startThreads();
    } 
}
