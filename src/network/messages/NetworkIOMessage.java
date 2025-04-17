package network.messages;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkIOMessage {
    public enum NetworkIOMessageType {
        TALK, FILE;
    }

    private NetworkIOMessageType type;
    private String stringField;
    private InetAddress destinationIp;

    public NetworkIOMessageType getType() {
        return type;
    }
    
    public String getStringField() {
        return stringField;
    }

    public InetAddress getDestinationIp() {
        return destinationIp;
    }

    // ****************************************************************************************************************
    // Builder pattern for NetworkIOMessage

    public static NetworkIOMessageIpSetter sendMessage(String message) {
        return new Builder(NetworkIOMessageType.TALK, message);
    }

    public static NetworkIOMessageIpSetter sendFile(String fileName) {
        return new Builder(NetworkIOMessageType.FILE, fileName);
    }

    public interface NetworkIOMessageIpSetter {
        NetworkIOMessage toIp(InetAddress destinationIp) throws UnknownHostException;
    }

    private static class Builder  implements NetworkIOMessageIpSetter {
        private NetworkIOMessageType type;
        private String stringField;
        private InetAddress destinationIp;

        @Override
        public NetworkIOMessage toIp(InetAddress destinationIp) throws UnknownHostException {
            this.destinationIp = destinationIp;
            return new NetworkIOMessage(this);
        }

        private Builder(NetworkIOMessageType type, String stringField) {
            this.type        = type;
            this.stringField = stringField;
        }
    }
    
    private NetworkIOMessage(Builder builder) {
        this.type          = builder.type;
        this.stringField   = builder.stringField;
        this.destinationIp = builder.destinationIp;
    }
}