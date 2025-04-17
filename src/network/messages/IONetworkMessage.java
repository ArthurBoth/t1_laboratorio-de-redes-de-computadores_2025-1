package network.messages;

public class IONetworkMessage {
    public enum IONetworkMessageType {
        EXIT, SEND_TALK, SEND_FILE, SEND_CHUNK, SEND_END;
    }

    private IONetworkMessageType type;
    private String stringField;
    private int chunkNumber;
    private long fileSize;
    private byte[] chunkData;

    public IONetworkMessageType getType() {
        return type;
    }

    public String getStringField() {
        return stringField;
    }

    public int getChunkNumber() {
        return chunkNumber;
    }

    public long getFileSize() {
        return fileSize;
    }

    public byte[] getChunkData() {
        return chunkData;
    }

    // ****************************************************************************************************************
    // Factory pattern for IONetworkMessage

    public static IONetworkMessage exit() {
        return new IONetworkMessage(IONetworkMessageType.EXIT, null, 0, 0, null);
    }

    public static IONetworkMessage talk(String message) {
        return new IONetworkMessage(IONetworkMessageType.SEND_TALK, message, 0, 0, null);
    }

    public static IONetworkMessage end(String hash) {
        return new IONetworkMessage(IONetworkMessageType.SEND_END, hash, 0, 0, null);
    }

    private IONetworkMessage(
            IONetworkMessageType type, String stringField, int chunkNumber, long fileSize, byte[] chunkData) {
        this.type = type;
        this.stringField = stringField;
        this.chunkNumber = chunkNumber;
        this.fileSize = fileSize;
        this.chunkData = chunkData;
    }

    // ****************************************************************************************************************
    // Builder pattern for IONetworkMessage
    
    public static IONetworkLongSetter file(String fileName) {
        return new Builder(IONetworkMessageType.SEND_FILE, fileName);
    }

    public static IONetworkByteSetter chunk(int chunkNumber) {
        return new Builder(IONetworkMessageType.SEND_CHUNK, chunkNumber);
    }

    public interface IONetworkLongSetter {
        IONetworkMessage fileSize(long fileSize);
    }

    public interface IONetworkByteSetter {
        IONetworkMessage chunkData(byte[] chunkData);
    }

    private static class Builder implements IONetworkLongSetter, IONetworkByteSetter {
        private IONetworkMessageType type;
        private String stringField;
        private int chunkNumber;
        private long fileSize;
        private byte[] chunkData;

        public Builder(IONetworkMessageType type, String stringField) {
            this.type        = type;
            this.stringField = stringField;

            chunkNumber = Integer.MIN_VALUE;
            fileSize    = Long.MIN_VALUE;
            chunkData   = null;
        }

        public Builder(IONetworkMessageType type, int chunkNumber) {
            this.type        = type;
            this.chunkNumber = chunkNumber;

            this.stringField = null;
            this.fileSize    = Long.MIN_VALUE;
            this.chunkData   = null;
        }

        @Override
        public IONetworkMessage fileSize(long fileSize) {
            this.fileSize = fileSize;
            return new IONetworkMessage(this);
        }

        @Override
        public IONetworkMessage chunkData(byte[] chunkData) {
            this.chunkData = chunkData;
            return new IONetworkMessage(this);
        }
    }

    private IONetworkMessage(Builder builder) {
        this.type        = builder.type;
        this.stringField = builder.stringField;
        this.chunkNumber = builder.chunkNumber;
        this.fileSize    = builder.fileSize;
        this.chunkData   = builder.chunkData;
    }
}
