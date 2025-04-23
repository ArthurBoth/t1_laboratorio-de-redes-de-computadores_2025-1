package messages.internal.receivedMessages;

import java.net.InetAddress;

import interfaces.visitors.FileMessageVisitor;
import interfaces.visitors.InternalMessageVisitor;
import utils.FileUtils;

import static utils.Constants.Strings.CHUNK_FORMAT;
import static utils.Constants.Strings.CHUNK_LOG_FORMAT;

public class InternalReceivedChunkMessage extends InternalReceivedFileRelated {
    private byte[] chunkData;
    private int sequenceNumber;

    public byte[] getData() {
        return chunkData;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalReceivedChunkMessage

    @Override
    public void accept(InternalMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(FileMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation 

    @Override
    public String getMessage() {
        return CHUNK_FORMAT.formatted(
            messageId,
            sequenceNumber,
            FileUtils.byteArrayToString(chunkData)
        );
    }

    @Override
    public String getPrettyMessage() {
        return CHUNK_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            sourceIp.getHostAddress(),
            messageId,
            sequenceNumber,
            FileUtils.byteArrayToString(chunkData),
            chunkData.length
        );
    }

    // ****************************************************************************************************
    // Builder pattern for InternalReceivedChunkMessage

    public static IntSetter create(Class<?> clazz, int messageId, byte[] chunkData) {
        return new Builder(clazz, messageId, chunkData);
    }

    public interface IntSetter {
        IpSetter<InternalReceivedChunkMessage> sequenceNumber(int sequenceNumber);
    }

    private static class Builder extends IpBuilder<InternalReceivedChunkMessage> implements IntSetter {
        private Class<?> clazz;
        private int messageId;
        private byte[] chunkData;
        private int sequenceNumber;
        private InetAddress sourceIp;

        private Builder(Class<?> clazz, int messageId, byte[] chunkData) {
            this.clazz     = clazz;
            this.messageId = messageId;
            this.chunkData = chunkData;
        }

        @Override
        public IpSetter<InternalReceivedChunkMessage> sequenceNumber(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
            return this;
        }

        @Override
        protected InternalReceivedChunkMessage self() {
            return new InternalReceivedChunkMessage(this);
        }
    }

    private InternalReceivedChunkMessage(Builder builder) {
        this.clazz          = builder.clazz;
        this.messageId      = builder.messageId;
        this.chunkData      = builder.chunkData;
        this.sequenceNumber = builder.sequenceNumber;
        this.sourceIp       = builder.sourceIp;
    }
}
