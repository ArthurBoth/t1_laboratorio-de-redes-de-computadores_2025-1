package messages.internal.received;

import messages.internal.received.InternalReceivedFileMessage.LongSetter;

public abstract class InternalReceivedIdMessage extends InternalReceivedMessage {
    protected int messageId;

    public int getMessageId() {
        return messageId;
    }

    // ****************************************************************************************************
    // Builder pattern for InternalReceivedIdMessage

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

    public static MessageSelection createWithId(Class<?> clazz, int messageId) {
        return new Builder(clazz, messageId);
    }
}
