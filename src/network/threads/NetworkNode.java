package network.threads;

public class NetworkNode {
    private final String IP_ADDRESS;
    private final int PORT;

    public NetworkNode(String ipAddress, int port) {
        this.IP_ADDRESS = ipAddress;
        this.PORT       = port;
    }

    public NetworkNode(String addressWithPort) {
        String[] ipParts = addressWithPort.split(":");

        this.IP_ADDRESS = ipParts[0];
        this.PORT       = Integer.parseInt(ipParts[1]);
    }

    public String getIpAddress() {
        return IP_ADDRESS;
    }

    public int getPort() {
        return PORT;
    }
}
