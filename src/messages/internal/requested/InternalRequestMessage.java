package messages.internal.requested;

import interfaces.visitors.internal.InternalMessageVisitor;
import interfaces.visitors.internal.InternalRequestMessageVisitor;
import messages.internal.InternalMessage;
import messages.internal.requested.InternalRequestSendNAckMessage.StringSetter;

public abstract class InternalRequestMessage extends InternalMessage {
    // ****************************************************************************************************
    // The InternalSentMessage class is the base class for messages that will, eventually, be sent over the 
    // network.
    //    It should be used to create messages that won't be sent over the network.
    // ****************************************************************************************************

    // ****************************************************************************************************
    // Visitor pattern for InternalRequestMessage

    @Override
    public final void accept(InternalMessageVisitor visitor) {
        visitor.visit(this);
    }

    public abstract void accept(InternalRequestMessageVisitor visitor);

    // ****************************************************************************************************
    // Builder pattern for InternalSentMessage

    public interface MessageSelection {
        InternalRequestExitMessage exit();
        InternalRequestSendMessage.IpSetter<InternalRequestSendTalkMessage> talk(String content);
        InternalRequestSendMessage.IpSetter<InternalRequestSendFileMessage> file(String fileName);
        InternalRequestSendMessage.IpSetter<InternalRequestSendAckMessage> ack(int messageId);
        InternalRequestSendNAckMessage.StringSetter nAck(int messageId);
    }

    private static final class Builder implements MessageSelection {
        private Class<?> clazz;

        private Builder(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public InternalRequestExitMessage exit() {
            return InternalRequestExitMessage.build(clazz);
        }

        @Override
        public InternalRequestSendMessage.IpSetter<InternalRequestSendTalkMessage> talk(String content) {
            return InternalRequestSendTalkMessage.create(clazz, content);
        }

        @Override
        public InternalRequestSendMessage.IpSetter<InternalRequestSendFileMessage> file(String fileName) {
            return InternalRequestSendFileMessage.create(clazz, fileName);
        }

        @Override
        public InternalRequestSendMessage.IpSetter<InternalRequestSendAckMessage> ack(int messageId) {
            return InternalRequestSendAckMessage.create(clazz, messageId);
        }

        @Override
        public StringSetter nAck(int messageId) {
            return InternalRequestSendNAckMessage.create(clazz, messageId);
        }
    }

    public static Builder create(Class<?> clazz) {
        return new Builder(clazz);
    }
}
