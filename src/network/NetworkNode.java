package network;

import java.net.InetAddress;

import utils.Constants.Configs;

public class NetworkNode {
    private final InetAddress ADDRESS;
    private final int PORT;
    private final String NAME;

    private int secondsSinceHeartbeatMessage;

    public InetAddress getAddress() {
        return ADDRESS;
    }

    public int getPort() {
        return PORT;
    }

    public String getName() {
        return NAME;
    }

    public int getSecondsSinceHeartbeatMessage() {
        return secondsSinceHeartbeatMessage;
    }

    /**
     * Increments the seconds since the last heartbeat message.
     * Returns {@code true} if the node should timeout (i.e., if it has not received a heartbeat message for too long).
     * @return {@code true} if the node should timeout, {@code false} otherwise
     */
    public boolean tickHeartbeat() {
        boolean shouldTimeout;

        secondsSinceHeartbeatMessage++;
        shouldTimeout = secondsSinceHeartbeatMessage > Configs.NODE_TIMEOUT_SEC;
        return shouldTimeout;
    }

    public void resetHeartbeat() {
        secondsSinceHeartbeatMessage = 0;
    }

    // ****************************************************************************************************
    // Factory pattern for InternalMessage

    private NetworkNode(InetAddress address, int port, String name) {
        this.ADDRESS = address;
        this.PORT    = port;
        this.NAME    = name;
        this.secondsSinceHeartbeatMessage = 0;
    }

    public static NetworkNode of(InetAddress address, int port, String name) {
        return new NetworkNode(address, port, name);
    }
}
