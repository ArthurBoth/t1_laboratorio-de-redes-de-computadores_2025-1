package network.threads;

import java.net.DatagramSocket;

import io.IOManager;

public class ThreadManager {
    private DatagramSocket socket;

    private SenderThread sender;
    private ReceiverThread receiver;
    private IOManager io;

    public void start() {

    }

    private void setup() {
        sender = new SenderThread(socket, null)
    } 
}
