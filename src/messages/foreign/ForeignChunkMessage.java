package messages.foreign;

import interfaces.visitors.EncoderVisitor;

public class ForeignChunkMessage extends ForeignMessage {
    private final int MESSAGE_ID;
    private int sequenceNumber;
    private byte[] chunkData;

    public int getMessageId() {
        return MESSAGE_ID;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public byte[] getData() {
        return chunkData;
    }

    // ****************************************************************************************************
    // Visitor pattern for ForeignChunkMessage

    @Override
    public byte[] encode(EncoderVisitor visitor) {
        return visitor.encode(this);
    }
    
    // ****************************************************************************************************
    // Builder pattern for ForeignChunkMessage

    public static ByteArraySetter create(int messageId, int sequenceNumber) {
        return new Builder(messageId, sequenceNumber);
    }

    public interface ByteArraySetter {
        IpSetter<ForeignChunkMessage> data(byte[] chunkData);
    }

    private static class Builder extends IpBuilder<ForeignChunkMessage> implements ByteArraySetter {
        private final int MESSAGE_ID;
        private int sequenceNumber;
        private byte[] chunkData;

        private Builder(int messageId, int sequenceNumber) {
            this.MESSAGE_ID     = messageId;
            this.sequenceNumber = sequenceNumber;
        }

        @Override
        public IpSetter<ForeignChunkMessage> data(byte[] chunkData) {
            this.chunkData = chunkData;
            return this;
        }

        @Override
        protected ForeignChunkMessage self() {
            return new ForeignChunkMessage(this);
        }
    }

    private ForeignChunkMessage(Builder builder) {
        this.MESSAGE_ID     = builder.MESSAGE_ID;
        this.sequenceNumber = builder.sequenceNumber;
        this.chunkData      = builder.chunkData;
    }
}
