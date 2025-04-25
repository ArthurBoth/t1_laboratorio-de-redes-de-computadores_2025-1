package network;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import messages.foreign.ForeignMessage;
import messages.internal.InternalMessage;
import network.threads.ThreadManager;
import utils.Constants;

public class NetworkManager {
    private DatagramSocket socket;

    private NetworkListener listener;
    private ThreadManager threadManager;
    private ConcurrentHashMap<InetAddress, Integer> activeNodes; // node -> seconds since last message

    public void start() throws SocketException {
        setup();
        listener.startListening();
        threadManager.stopThreads();
        socket.close();
    }

    private void setup() throws SocketException {
        BlockingQueue<InternalMessage> ioSenderQueue;
        BlockingQueue<InternalMessage> ioReceiverQueue;
        BlockingQueue<InternalMessage> udpReceiverQueue;
        BlockingQueue<ForeignMessage> udpSenderQueue;

        socket        = new DatagramSocket(Constants.Configs.DEFAULT_PORT);
        activeNodes   = new ConcurrentHashMap<InetAddress, Integer>();
        listener      = new NetworkListener(activeNodes);
        threadManager = new ThreadManager(socket, activeNodes);

        udpSenderQueue   = threadManager.createSender();
        udpReceiverQueue = threadManager.createReceiver();
        ioSenderQueue    = new LinkedBlockingQueue<InternalMessage>();
        ioReceiverQueue  = threadManager.createIO(ioSenderQueue);

        listener.setIoReceiverQueue(ioReceiverQueue);
        listener.setIoSenderQueue(ioSenderQueue);
        listener.setUdpReceiverQueue(udpReceiverQueue);
        listener.setUdpSenderQueue(udpSenderQueue);
        
        socket.setBroadcast(true);
        threadManager.startThreads();
    } 
}
