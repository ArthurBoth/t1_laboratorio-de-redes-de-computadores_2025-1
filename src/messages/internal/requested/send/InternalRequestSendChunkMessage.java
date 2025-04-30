package messages.internal.requested.send;

import static utils.Constants.Strings.CHUNK_SENDING_LOG_FORMAT;
import static utils.Constants.Strings.SIMPLE_CHUNK_FORMAT;

import interfaces.visitors.LoggerVisitor;
import interfaces.visitors.internal.InternalRequestMessageVisitor;
import utils.FileUtils;

public class InternalRequestSendChunkMessage extends InternalRequestSendMessage {
    private int seqNumber;
    private byte[] chunk;

    public int getSequenceNumber() {
        return seqNumber;
    }

    public byte[] getChunk() {
        return chunk;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalRequestSendChunkMessage

    @Override
    public void accept(InternalRequestMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(LoggerVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return SIMPLE_CHUNK_FORMAT.formatted(
            seqNumber,
            FileUtils.byteArrayToString(chunk)
        );
    }

    @Override
    public String getPrettyMessage() {
        return CHUNK_SENDING_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            seqNumber,
            chunk.length
        );
    }

    // ****************************************************************************************************
    // Builder pattern for InternalRequestSendChunkMessage

    protected static ByteArraySetter create(Class<?> clazz, int seqNumber) {
        return new Builder(clazz, seqNumber);
    }

    public interface ByteArraySetter {
        IpSetter<InternalRequestSendChunkMessage> data(byte[] chunk);
    }

    private static class Builder extends IpBuilder<InternalRequestSendChunkMessage> implements ByteArraySetter {
        private Class<?> clazz;
        private int seqNumber;
        private byte[] chunk;

        private Builder(Class<?> clazz, int seqNumber) {
            this.clazz     = clazz;
            this.seqNumber = seqNumber;
        }

        @Override
        public InternalRequestSendChunkMessage self() {
            return new InternalRequestSendChunkMessage(this);
        }

        @Override
        public IpSetter<InternalRequestSendChunkMessage> data(byte[] chunk) {
            this.chunk = chunk;
            return this;
        }
    }

    private InternalRequestSendChunkMessage(Builder builder) {
        this.clazz         = builder.clazz;
        this.seqNumber     = builder.seqNumber;
        this.chunk         = builder.chunk;
        this.destinationIp = builder.destinationIp;
        this.port          = builder.port;
    }
}
