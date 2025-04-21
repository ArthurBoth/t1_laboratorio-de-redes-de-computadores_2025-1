package messages.foreign;

import interfaces.visitors.EncoderVisitor;
import interfaces.visitors.ForeignMessageVisitor;
import utils.FileUtils;

import static utils.Constants.Strings.CHUNK_FORMAT;
import static utils.Constants.Strings.CHUNK_LOG_FORMAT;

public class ForeignChunkMessage extends ForeignMessage {
    private final int MESSAGE_ID;
    private int chunkNumber;
    private byte[] chunkData;

    public int getMessageId() {
        return MESSAGE_ID;
    }

    public int getSequenceNumber() {
        return chunkNumber;
    }

    public byte[] getData() {
        return chunkData;
    }

    // **************************************************************************************************************
    // Visitor pattern for ForeignChunkMessage

    @Override
    public void accept(ForeignMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public byte[] encode(EncoderVisitor visitor) {
        return visitor.encode(this);
    }

    // **************************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return CHUNK_FORMAT.formatted(
            MESSAGE_ID,
            chunkNumber,
            FileUtils.byteArrayToString(chunkData)
            );
    }

    @Override
    public String getPrettyMessage() {
        return CHUNK_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            destinationIp.getHostAddress(),
            MESSAGE_ID,
            chunkNumber,
            FileUtils.byteArrayToString(chunkData),
            chunkData.length
            );
    }
    
    // **************************************************************************************************************
    // Builder pattern for ForeignChunkMessage

    public static ByteArraySetter create(Class<?> clazz, int messageId, int chunkNumber) {
        return new Builder(clazz, messageId, chunkNumber);
    }

    public interface ByteArraySetter {
        IpSetter<ForeignChunkMessage> data(byte[] chunkData);
    }

    private static class Builder extends IpBuilder<ForeignChunkMessage> implements ByteArraySetter {
        private final int MESSAGE_ID;
        private Class<?> clazz;
        private int chunkNumber;
        private byte[] chunkData;

        private Builder(Class<?> clazz, int messageId, int chunkNumber) {
            this.MESSAGE_ID  = messageId;
            this.clazz       = clazz;
            this.chunkNumber = chunkNumber;
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
        this.MESSAGE_ID  = builder.MESSAGE_ID;
        this.clazz       = builder.clazz;
        this.chunkNumber = builder.chunkNumber;
        this.chunkData   = builder.chunkData;
    }
}
