package messages.internal;

import interfaces.visitors.InternalMessageVisitor;
import interfaces.visitors.LoggerVisitor;
import interfaces.visitors.MessageVisitor;
import messages.ThreadMessage;
import messages.internal.receivedMessages.InternalReceivedIdMessage;
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
        InternalReceivedMessage.MessageSelection receivedMessage();
        InternalReceivedIdMessage.MessageSelection receivedMessage(int messageId);
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
        public messages.internal.receivedMessages.InternalReceivedMessage.MessageSelection receivedMessage() {
            return InternalReceivedMessage.create(clazz);
        }

        @Override
        public InternalReceivedIdMessage.MessageSelection receivedMessage(int messageId) {
            return InternalReceivedMessage.create(clazz, messageId);
        }
    }

    public static Builder instance(Class<?> clazz) {
        return new Builder(clazz);
    }
}
