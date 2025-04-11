package network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.IOManager;
import network.threads.ThreadMessage;

public class NetworkManager {
    private final String IP_ADDRESS;

    private IOManager io;
    private BlockingQueue<ThreadMessage> messageQueue;

    public NetworkManager() throws UnknownHostException {
        IP_ADDRESS = InetAddress.getLocalHost().getHostAddress();

        io           = new IOManager(messageQueue);
        messageQueue = new LinkedBlockingQueue<ThreadMessage>();
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
        // TODO
    }

    public void start() {
        try {
            setup();
        } catch (InterruptedException e) {
            io.endExecution();
        }
    }
}
