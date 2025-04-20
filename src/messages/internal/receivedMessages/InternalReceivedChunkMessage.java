package messages.internal.receivedMessages;

import java.net.InetAddress;

import interfaces.visitors.InternalMessageVisitor;
import utils.FileUtils;

import static utils.Constants.Strings.CHUNK_LOG_FORMAT;

public class InternalReceivedChunkMessage extends InternalReceivedMessage {
    private byte[] data;    
    private int sequenceNumber;

    public byte[] getData() {
        return data;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    // ***************************************************************************************************************
    // Visitor pattern for InternalReceivedChunkMessage

    public void accept(InternalMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ***************************************************************************************************************
    // Loggable interface implementation 

    @Override
    public String getMessage() {
        return CHUNK_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            sourceIp.getHostAddress(), 
            sequenceNumber,
            FileUtils.byteArrayToString(data),
            data.length
        );
    }

    // ***************************************************************************************************************
    // Builder pattern for InternalReceivedChunkMessage

    public static IntSetter create(Class<?> clazz, int messageId, byte[] data) {
        return new Builder(clazz, messageId, data);
    }

    public interface IntSetter {
        IpSetter<InternalReceivedChunkMessage> sequenceNumber(int sequenceNumber);
    }

    private static class Builder extends IpBuilder<InternalReceivedChunkMessage> implements IntSetter {
        private Class<?> clazz;
        private int messageId;
        private byte[] data;
        private int sequenceNumber;
        private InetAddress sourceIp;

        private Builder(Class<?> clazz, int messageId, byte[] data) {
            this.clazz     = clazz;
            this.messageId = messageId;
            this.data      = data;
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
        this.data           = builder.data;
        this.sequenceNumber = builder.sequenceNumber;
        this.sourceIp       = builder.sourceIp;
    }
}
