package network.threads;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkNode {
    private final InetAddress IP_ADDRESS;
    private final int PORT;

    public NetworkNode(String ipAddress, int port) throws UnknownHostException {
        this.IP_ADDRESS = InetAddress.getByName(ipAddress);
        this.PORT       = port;
    }

    public InetAddress getIpAddress() {
        return IP_ADDRESS;
    }

    public int getPort() {
        return PORT;
    }

    public String getFullAddress() {
        return String.format("%s:%d", IP_ADDRESS, PORT);
    }
}
