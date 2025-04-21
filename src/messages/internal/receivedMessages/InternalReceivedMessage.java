package messages.internal.receivedMessages;

import java.net.InetAddress;

import messages.internal.InternalMessage;
import messages.internal.receivedMessages.InternalReceivedFileMessage.LongSetter;

public abstract class InternalReceivedMessage extends InternalMessage {
    // **************************************************************************************************************
    // The InternalReceivedMessage class is the base class for all messages received from the network.
    //    It should be used to map the messages received from the network to the internal messages of the system.
    // **************************************************************************************************************
    protected InetAddress sourceIp;
    protected int messageId;

    public InetAddress getSourceIp() {
        return sourceIp;
    }

    public int getMessageId() {
        return messageId;
    }

    // **************************************************************************************************************
    // Builder pattern for InternalReceivedMessage

    public interface MessageSelection {
        InternalReceivedTalkMessage.IpSetter<InternalReceivedTalkMessage> talk(String content);
        InternalReceivedFileMessage.LongSetter file(String fileName);
        InternalReceivedChunkMessage.IntSetter chunk(byte[] data);
        InternalReceivedEndMessage.IpSetter<InternalReceivedEndMessage> end(String fileHash);
    }

    private static final class Builder implements MessageSelection {
        private Class<?> clazz;
        private int messageId;

        private Builder(Class<?> clazz, int messageId) {
            this.clazz     = clazz;
            this.messageId = messageId;
        }

        @Override
        public InternalReceivedTalkMessage.IpSetter<InternalReceivedTalkMessage> talk(String content) {
            return InternalReceivedTalkMessage.create(clazz, messageId, content);
        }

        @Override
        public LongSetter file(String fileName) {
            return InternalReceivedFileMessage.create(clazz, messageId, fileName);
        }

        @Override
        public InternalReceivedChunkMessage.IntSetter chunk(byte[] data) {
            return InternalReceivedChunkMessage.create(clazz, messageId, data);
        }

        @Override
        public InternalReceivedEndMessage.IpSetter<InternalReceivedEndMessage> end(String fileHash) {
            return InternalReceivedEndMessage.create(clazz, messageId, fileHash);
        }
    }

    public static Builder create(Class<?> clazz, int messageId) {
        return new Builder(clazz, messageId);
    }
    
    // **************************************************************************************************************
    // Abstract Builder pattern for InternalReceivedMessage subclasses


    public interface IpSetter<T extends InternalReceivedMessage> {
        T from(InetAddress sourceIp);
    }

    protected static abstract class IpBuilder<T extends InternalReceivedMessage> implements IpSetter<T> {
        protected InetAddress sourceIp;

        @Override
        public final T from(InetAddress sourceIp) {
            this.sourceIp = sourceIp;
            return self();
        }

        protected abstract T self();
    } 
}
