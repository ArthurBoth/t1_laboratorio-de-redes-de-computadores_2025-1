package messages.internal;

import interfaces.visitors.InternalMessageVisitor;
import interfaces.visitors.LoggerVisitor;
import interfaces.visitors.MessageVisitor;
import messages.ThreadMessage;
import messages.internal.acknowledgments.AckMessage;
import messages.internal.acknowledgments.NAckMessage;
import messages.internal.receivedMessages.InternalReceivedMessage;
import messages.internal.sentMessages.InternalSentMessage;

public abstract class InternalMessage extends ThreadMessage {
    // **************************************************************************************************************
    // The InternalMessage class is the base class for all internal messages in the system.
    //    It should be used to create messages that won't be sent over the network.
    // **************************************************************************************************************

    // ***************************************************************************************************************
    // Visitor pattern for InternalMessage

    public abstract void accept(InternalMessageVisitor visitor);

    @Override
    public void accept(LoggerVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(MessageVisitor visitor) {
        visitor.visit(this);
    }

    // ***************************************************************************************************************
    // Builder pattern for InternalMessage

    public interface MessageSelection {
        // sent messages
        InternalSentMessage.MessageSelection sendMessage();

        // received messages
        InternalReceivedMessage.MessageSelection receivedMessage(int messageId);
        AckMessage.IpSetter<AckMessage> ack(int messageId);
        NAckMessage.IpSetter<NAckMessage> nack(int messageId);
    }

    private static class Builder implements MessageSelection {
        private Class<?> clazz;

        private Builder(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public InternalSentMessage.MessageSelection sendMessage() {
            return InternalSentMessage.create(clazz);
        }

        @Override
        public InternalReceivedMessage.MessageSelection receivedMessage(int messageId) {
            return InternalReceivedMessage.create(clazz, messageId);
        }

        @Override
        public AckMessage.IpSetter<AckMessage> ack(int messageId) {
            return AckMessage.create(clazz, messageId);
        }

        @Override
        public NAckMessage.IpSetter<NAckMessage> nack(int messageId) {
            return NAckMessage.create(clazz, messageId);
        }
    }

    public static Builder instance(Class<?> clazz) {
        return new Builder(clazz);
    }
}
