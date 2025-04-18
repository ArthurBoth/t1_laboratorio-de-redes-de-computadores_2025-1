package network.messages.internal;

import java.net.InetAddress;

public class IONetworkMessage extends InternalMessage {
    public enum IONetworkMessageType {
        EXIT, SEND_TALK, SEND_FILE, SEND_CHUNK, SEND_END, SEND_ACK, SEND_NACK;
    }

    private InetAddress destinationIp;
    private IONetworkMessageType type;
    private String stringField;
    private int intField;
    private long fileSize;
    private byte[] chunkData;

    public InetAddress getDestinationIp() {
        return destinationIp;
    }

    public IONetworkMessageType getType() {
        return type;
    }

    public String getStringField() {
        return stringField;
    }

    public int getChunkNumber() {
        return intField;
    }

    public long getFileSize() {
        return fileSize;
    }

    public byte[] getChunkData() {
        return chunkData;
    }

    // **************************************************************************************************************
    // Builder pattern for IONetworkMessage
    public interface MessageSelection {
        IONetworkMessage exit();
        StringSetter sendTalk(InetAddress destinationIp);
        FileNameSetter sendFile(InetAddress destinationIp);
        SeqNumberSetter sendChunk(InetAddress destinationIp);
        StringSetter sendEnd(InetAddress destinationIp);
        AckIdSetter sendAck(InetAddress destinationIp);
        NAckIdSetter sendNAck(InetAddress destinationIp);
    }

    public interface StringSetter {
        IONetworkMessage string(String message);
    }

    public interface FileNameSetter {
        FileSizeSetter fileName(String fileName);
    }

    public interface FileSizeSetter {
        IONetworkMessage fileSize(long fileSize);
    }

    public interface SeqNumberSetter {
        DataSetter sequenceNumber(int chunkNumber);
    }

    public interface DataSetter {
        IONetworkMessage data(byte[] chunkData);
    }

    public interface AckIdSetter {
        IONetworkMessage ackId(int messageId);
    }

    public interface NAckIdSetter {
        StringSetter nAckId(int messageId);
    }

    protected static class Builder 
                    implements MessageSelection, StringSetter, FileNameSetter, FileSizeSetter, 
                                SeqNumberSetter, DataSetter, AckIdSetter, NAckIdSetter {
        private InetAddress destinationIp;
        private IONetworkMessageType type;
        private String stringField;
        private int intField;
        private long fileSize;
        private byte[] chunkData;

        protected Builder() {
            this.destinationIp = null;
            this.type          = null;
            this.stringField   = null;
            this.intField      = Integer.MIN_VALUE;
            this.fileSize      = Integer.MIN_VALUE;
            this.chunkData     = null;
        }

        @Override
        public IONetworkMessage exit() {
            this.type = IONetworkMessageType.EXIT;
            return new IONetworkMessage(this);
        }

        @Override
        public StringSetter sendTalk(InetAddress destinationIp) {
            this.type          = IONetworkMessageType.SEND_TALK;
            this.destinationIp = destinationIp;
            return this;
        }

        @Override
        public FileNameSetter sendFile(InetAddress destinationIp) {
            this.type          = IONetworkMessageType.SEND_FILE;
            this.destinationIp = destinationIp;
            return this;
        }

        @Override
        public SeqNumberSetter sendChunk(InetAddress destinationIp) {
            this.type          = IONetworkMessageType.SEND_CHUNK;
            this.destinationIp = destinationIp;
            return this;
        }

        @Override
        public StringSetter sendEnd(InetAddress destinationIp) {
            this.type          = IONetworkMessageType.SEND_END;
            this.destinationIp = destinationIp;
            return this;
        }

        @Override
        public AckIdSetter sendAck(InetAddress destinationIp) {
            this.type          = IONetworkMessageType.SEND_ACK;
            this.destinationIp = destinationIp;
            return this;
        }

        @Override
        public NAckIdSetter sendNAck(InetAddress destinationIp) {
            this.type          = IONetworkMessageType.SEND_NACK;
            this.destinationIp = destinationIp;
            return this;
        }

        @Override
        public IONetworkMessage string(String message) {
            this.stringField = message;
            return new IONetworkMessage(this);
        }

        @Override
        public FileSizeSetter fileName(String fileName) {
            this.stringField = fileName;
            return this;
        }

        @Override
        public IONetworkMessage fileSize(long fileSize) {
            this.fileSize = fileSize;
            return new IONetworkMessage(this);
        }

        @Override
        public DataSetter sequenceNumber(int chunkNumber) {
            this.intField = chunkNumber;
            return this;
        }

        @Override
        public IONetworkMessage data(byte[] chunkData) {
            this.chunkData = chunkData;
            return new IONetworkMessage(this);
        }

        @Override
        public IONetworkMessage ackId(int messageId) {
            this.intField = messageId;
            return new IONetworkMessage(this);
        }

        @Override
        public StringSetter nAckId(int messageId) {
            this.intField = messageId;
            return this;
        }
    }

    private IONetworkMessage(Builder builder) {
        this.destinationIp = builder.destinationIp;
        this.type          = builder.type;
        this.stringField   = builder.stringField;
        this.intField      = builder.intField;
        this.fileSize      = builder.fileSize;
        this.chunkData     = builder.chunkData;
    }

    // *************************************************************************************************************
    // Loggable interface for InternalMessage

    @Override
    public String getActualMessage() {
        return type.toString();
    }
}
