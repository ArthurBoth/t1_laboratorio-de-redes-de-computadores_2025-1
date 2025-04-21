package network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import messages.ThreadMessage;
import network.threads.NetworkNode;

public class NetworkListener {
    BlockingQueue<ThreadMessage> IOReceiverQueue;
    BlockingQueue<ThreadMessage> IOSenderQueue;
    BlockingQueue<ThreadMessage> UDPReceiverQueue;

    ConcurrentHashMap<NetworkNode, Integer> activeNodes;

    public NetworkListener() {
    }

    private void setup() {
        // TODO
        /*
         * Start Every thread 
         * - NetworkSender
         * - NetworkReceiver
         * - IO manager
         *      (Io manager will start the console on another thread)
         */
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void start() {
        setup();
        // TODO
        /*
         * Start listening to both the network and the console
         * Use visitor pattern to Ack or Nack foreign messages
         * also process console messages
         */
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
