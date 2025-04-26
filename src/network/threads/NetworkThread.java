package network.threads;

import java.net.DatagramSocket;

public abstract class NetworkThread extends AppThread {
    protected DatagramSocket socket;
    
    protected NetworkThread(DatagramSocket socket) {
        running = true;
        this.socket = socket;
    }
}
