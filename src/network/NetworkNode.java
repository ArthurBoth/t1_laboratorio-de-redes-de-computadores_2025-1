package network;

import static utils.Constants.Strings.CUSTOM_IP_NAME_FORMAT;

import java.net.InetAddress;

import utils.Constants.Configs;

public class NetworkNode {
    private static int customNodeCount = 0;

    private final InetAddress ADDRESS;
    private final int PORT;
    private final String NAME;
    public final boolean ARTIFICIAL_NODE;

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

    private NetworkNode(InetAddress address, int port, String name, boolean artificial) {
        this.ADDRESS         = address;
        this.PORT            = port;
        this.NAME            = name;
        this.ARTIFICIAL_NODE = artificial;

        this.secondsSinceHeartbeatMessage = 0;
    }

    public static NetworkNode of(InetAddress address, int port, String name) {
        return new NetworkNode(address, port, name, false);
    }

    public static NetworkNode of(InetAddress address, int port) {
        return new NetworkNode(address, port, getCustomNodeName(), true);
    }

    private static String getCustomNodeName() {
        return CUSTOM_IP_NAME_FORMAT.formatted(customNodeCount++);
    }
}
