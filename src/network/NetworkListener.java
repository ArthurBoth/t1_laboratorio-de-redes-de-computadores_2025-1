package network;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

import messages.ThreadMessage;
import messages.internal.acknowledgments.AckMessage;
import messages.internal.sentMessages.InternalSentFileMessage;
import network.threads.NetworkNode;

public class NetworkListener {
//     BlockingQueue<IONetworkMessage> IOReceiverQueue;
//     BlockingQueue<ForeignMessage> IOSenderQueue;
//     BlockingQueue<ForeignMessage> UDPReceiverQueue;

    ConcurrentHashMap<NetworkNode, Integer> activeNodes;

    public NetworkListener() {
    }

    private void setup() {
        InetAddress localHost = null;
        InternalSentFileMessage a =
        ThreadMessage.internalMessage()
            .sentMessage()
            .sendFile(null)
            .to(localHost);

        AckMessage ack = ThreadMessage.internalMessage().ack(0).from(localHost);

        a = a.fileData(null).fileHash(null);



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
