package messages;

import interfaces.Loggable;
import messages.foreign.ForeignMessage;
import messages.internal.InternalMessage;

public abstract class ThreadMessage implements Loggable {
    protected Class<?> clazz;

    // ***************************************************************************************************************
    // Factory pattern for ThreadMessage

    public static InternalMessage.MessageSelection internalMessage(Class<?> clazz) {
        return InternalMessage.instance(clazz);
    }
    
    public static ForeignMessage.MessageSeleciton foreignMessage(Class<?> clazz) {
        return ForeignMessage.instance(clazz);
    }
}
