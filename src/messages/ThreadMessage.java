package messages;

import interfaces.visitors.MessageVisitor;
import messages.foreign.ForeignHeartbeatMessage;
import messages.foreign.ForeignMessage;
import messages.internal.InternalMessage;

public abstract class ThreadMessage {
    protected Class<?> clazz;

    // ****************************************************************************************************
    // Visitor pattern for ThreadMessage

    public abstract void accept(MessageVisitor visitor);

    // ****************************************************************************************************
    // Factory pattern for ThreadMessage

    /**
     * Factory method to create an InternalMessage instance.
     * @param clazz is the class of the caller, use `this.getClass()` as a Parameter.
     * @return the an interface for choosing the type of InternalMessage.
     * @see InternalMessage
     */
    public static InternalMessage.MessageSelection internalMessage(Class<?> clazz) {
        return InternalMessage.instance(clazz);
    }
    
    /**
     * Factory method to create a ForeignMessage instance.
     * @param messageId is id of the message.
     * @return the an interface for choosing the type of ForeignMessage.
     * @see ForeignMessage
     */
    public static ForeignMessage.IdMessageSeleciton foreignMessage(int messageId) {
        return ForeignMessage.instance(messageId);
    }

    /**
     * Factory method to create a ForeignHeartbeatMessage instance.
     * @return the an interface setting an Ip destination for the ForeignHeartbeatMessage.
     * @see ForeignHeartbeatMessage
     */
    public static ForeignMessage.IdlessMessageSeleciton foreignMessage() {
        return ForeignMessage.instance();
    }
}
