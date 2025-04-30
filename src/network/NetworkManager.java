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
import utils.ConsoleLogger;
import utils.Constants.Configs;
import utils.Exceptions.EndExecutionException;

public class NetworkManager {
    private DatagramSocket socket;

    private NetworkListener listener;
    private ThreadManager threadManager;
    private ConcurrentHashMap<InetAddress, NetworkNode> activeNodes; // ip -> node

    public void start() throws SocketException {
        setup();
        try {
            listener.startListening();
        } catch (EndExecutionException e) {
            // EndExecutionException is thrown when the program is terminated
            // and is not an error, so we don't need to log it
        } catch (Exception e) {
            ConsoleLogger.logError(e);
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        threadManager.stopThreads();
        socket.close();
    }

    private void setup() throws SocketException {
        BlockingQueue<InternalMessage> ioSenderQueue;
        BlockingQueue<InternalMessage> ioReceiverQueue;
        BlockingQueue<InternalMessage> udpReceiverQueue;
        BlockingQueue<ForeignMessage> udpSenderQueue;
        BlockingQueue<InternalMessage> timerReceiverQueue;

        socket        = new DatagramSocket(Configs.DEFAULT_PORT, Configs.IP_ADDRESS);
        activeNodes   = new ConcurrentHashMap<InetAddress, NetworkNode>();
        listener      = new NetworkListener(activeNodes);
        threadManager = new ThreadManager(socket, activeNodes);

        udpSenderQueue     = threadManager.createSender();
        udpReceiverQueue   = threadManager.createReceiver();
        timerReceiverQueue = threadManager.createTimer(udpSenderQueue);
        ioSenderQueue      = new LinkedBlockingQueue<InternalMessage>();
        ioReceiverQueue    = threadManager.createIo(ioSenderQueue);

        listener.setIoReceiverQueue(ioReceiverQueue);
        listener.setIoSenderQueue(ioSenderQueue);
        listener.setUdpReceiverQueue(udpReceiverQueue);
        listener.setUdpSenderQueue(udpSenderQueue);
        listener.setTimerReceiverQueue(timerReceiverQueue);
        listener.setup(threadManager.messagesMap());
        
        socket.setBroadcast(true);
        threadManager.startThreads();
    } 
}
