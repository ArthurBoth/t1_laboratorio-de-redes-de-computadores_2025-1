package messages.internal.requested;

import interfaces.visitors.internal.InternalMessageVisitor;
import interfaces.visitors.internal.InternalRequestMessageVisitor;
import messages.internal.InternalMessage;
import messages.internal.requested.send.InternalRequestSendMessage;

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
        InternalRequestResendMessage resend(int messageId);
        InternalRequestSendMessage.MessageSelection send();
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
        public InternalRequestResendMessage resend(int messageId) {
            return InternalRequestResendMessage.build(clazz, messageId);
        }

        @Override
        public messages.internal.requested.send.InternalRequestSendMessage.MessageSelection send() {
            return InternalRequestSendMessage.build(clazz);
        }
    }

    public static Builder create(Class<?> clazz) {
        return new Builder(clazz);
    }
}
