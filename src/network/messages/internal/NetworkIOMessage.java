package network.messages.internal;

public class NetworkIOMessage {
    public enum NetworkIOMessageType {
        FILE_ACK, FILE_NACK;
    }

    private NetworkIOMessageType type;
    private String fileName;

    public NetworkIOMessageType getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    // **************************************************************************************************************
    // Builder pattern for NetworkIOMessage
    public interface MessageSelection {
        NetworkIOMessage ack(String fileName);
        NetworkIOMessage nAck(String fileName);
    }

    protected static class Builder implements MessageSelection {
        private NetworkIOMessageType type;
        private String fileName;

        protected Builder() {
            this.type = null;
            this.fileName = null;
        }

        @Override
        public NetworkIOMessage ack(String fileName) {
            this.type = NetworkIOMessageType.FILE_ACK;
            this.fileName = fileName;
            return new NetworkIOMessage(this);
        }

        @Override
        public NetworkIOMessage nAck(String fileName) {
            this.type     = NetworkIOMessageType.FILE_NACK;
            this.fileName = fileName;
            return new NetworkIOMessage(this);
        }
    }

    protected NetworkIOMessage(Builder builder) {
        this.type     = builder.type;
        this.fileName = builder.fileName;
    }
}
