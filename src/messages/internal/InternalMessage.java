package messages.internal;

import interfaces.Loggable;
import interfaces.visitors.LoggerVisitor;
import interfaces.visitors.MessageVisitor;
import interfaces.visitors.internal.InternalMessageVisitor;
import messages.ThreadMessage;
import messages.internal.received.InternalReceivedIdMessage;
import messages.internal.received.InternalReceivedMessage;
import messages.internal.requested.InternalRequestMessage;

public abstract class InternalMessage extends ThreadMessage implements Loggable {
    // ****************************************************************************************************
    // The InternalMessage class is the base class for all internal messages in the system.
    //    It should be used to create messages that won't be sent over the network.
    // ****************************************************************************************************

    // ****************************************************************************************************
    // Visitor pattern for InternalMessage

    public abstract void accept(InternalMessageVisitor visitor);
    public abstract void accept(LoggerVisitor visitor);

    @Override
    public void accept(MessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Builder pattern for InternalMessage

    public interface MessageSelection {
        // sent messages
        InternalRequestMessage.MessageSelection request();

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
        public InternalRequestMessage.MessageSelection request() {
            return InternalRequestMessage.create(clazz);
        }

        @Override
        public messages.internal.received.InternalReceivedMessage.MessageSelection receivedMessage() {
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
