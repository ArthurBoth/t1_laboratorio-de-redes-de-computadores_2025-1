package network;

import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import constants.Constants;
import io.IOManager;
import network.messages.ExternalMessage;
import network.messages.InternalMessage;
import network.messages.ThreadMessage;
import network.threads.NetworkNode;

public class NetworkManager {
    // Application variables
    private IOManager io;
    private BlockingQueue<ThreadMessage> sendMessages;            // only-send
    private BlockingQueue<ThreadMessage> receiveMessages;         // only-receive

    // Network variables
    private ConcurrentHashMap<NetworkNode, Integer> activeNodes;  // IP:PORT -> seconds since last message
    private DatagramSocket socket;                                // Socket for sending and receiving messages

    public NetworkManager() {        
        activeNodes     = new ConcurrentHashMap<NetworkNode, Integer>();
        sendMessages    = new LinkedBlockingQueue<ThreadMessage>();
        receiveMessages = new LinkedBlockingQueue<ThreadMessage>();
        io              = new IOManager(receiveMessages, activeNodes);
    }
    
    /* TODO: 
     * - Implement HEARTBEAT sending
     *   > Send a heartbeat upon initialization (broadcast)
     *   > Send a heartbeat to the network (broadcast) every 5 seconds
     * - Implement HEARTBEAT receiving
     *   > Update the list of active nodes in the network
     * - Implement HEARTBEAT timeout
     *   > Remove nodes from the list of active nodes if no HEARTBEAT is received for 10 seconds
     * 
     * - Implement TALK sending
     *   > Send a talk message to a single node 
     *   > TALK message will be an input from the user (Strings only)
     * - Implement TALK receiving
     *   > Messages must be logged to a file
     *   > Messages must be responded to with an ACK message
     * 
     * - Implement FILE sending
     *   > Send a request to a single node to send a file
     *   > Wait for ACK response from the node 
     * - Implement FILE receiving
     *   > Must answer with an ACK message
     * 
     * - Implement CHUNCK sending
     *   > Split a file into chunks and send them to a single node
     *     > Don't wait for ACK response from the node
     *     > Wait for ACK response from the node
     *   > If ACK response is not received (whithin 10 sec), resend the chunk
     * * - Implement CHUNCK receiving
     *   > Must answer every CHUNK with an ACK message  
     *   > Duplicate chunks must be ignored
     * 
     * - Implement END sending
     *   > Must be sent after all chunks are sent
     *   > Send a SHA256 hash of the file to the node
     * - Implement END receiving
     *   > Rebuild the file from the chunks received
     *   > Check the SHA256 hash of the file received with the one sent
     *     > If the hashes are equal, the file is complete and valid
     *     > If the hashes are not equal, the file is corrupted
     *   > If the file is complete and valid, send a ACK message to the sender
     *   > If the file is corrupted, send a NACK message to the sender
     * 
     * - Implement ACK sending
     *   > Must be sent after every TALK, FILE, CHUNK and END message received
     *   > Must contain the Id of the acknowledged message
     * - Implement NACK sending
     *   > Must be sent after if file received is corrupted
     *     OR
     *   > Must be sent if message is not recognized
     *   > Must contain the Id of the non-acknowledged message
     * 
    */

    private void setup() {
        try {
            socket = new DatagramSocket(Constants.Configs.DEFAULT_PORT);
            socket.setBroadcast(true);

            // TODO: create threads
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: initialize threads
        new Thread(() -> io.run()).start();
    }

    public void start() {
        boolean running = true;
        ThreadMessage message;

        setup();
        while(running) {
            try {
                message = receiveMessages.poll(Constants.Configs.THREAD_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                if (message != null) running = processMessage(message);
            } catch (InterruptedException e) {
                return;
            }
        }
    }


    private boolean processMessage(ThreadMessage message) {
        if (message.isExternalMessage()) {
            return processExternalMessage((ExternalMessage) message);
        } else {
            return processInternalMessage((InternalMessage) message);
        }
    }

    private boolean processExternalMessage(ExternalMessage message) {
        boolean keepRunning = true;

        switch (message.getType()) {
            case TALK -> {}
            default -> {throw new IllegalArgumentException("Invalid external message type: " + message.getType());}
        }
        return keepRunning;
    }

    private boolean processInternalMessage(InternalMessage message) {
        boolean keepRunning = true;

        switch (message.getType()) {
            case EXIT -> {keepRunning = false;}
            default   -> {throw new IllegalArgumentException("Invalid internal message type: " + message.getType());}
        }
        return keepRunning;
    }
}
