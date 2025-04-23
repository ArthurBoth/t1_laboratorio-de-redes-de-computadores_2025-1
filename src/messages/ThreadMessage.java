package messages;

import interfaces.visitors.MessageVisitor;
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
     * * Factory method to create an InternalMessage instance.
     * @param clazz is the class of the caller, use `this.getClass()` as a Parameter.
     * @return the instance of a new InternalMessage.
     * @see InternalMessage
     */
    public static InternalMessage.MessageSelection internalMessage(Class<?> clazz) {
        return InternalMessage.instance(clazz);
    }
    
    /**
     * * Factory method to create a ForeignMessage instance.
     * @param clazz is the class of the caller, use ```this.getClass()``` as a Parameter.
     * @return the instance of a new ForeignMessage.
     * @see ForeignMessage
     */
    public static ForeignMessage.MessageSeleciton foreignMessage(Class<?> clazz) {
        return ForeignMessage.instance(clazz);
    }
}
