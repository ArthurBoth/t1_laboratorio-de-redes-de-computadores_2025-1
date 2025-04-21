package messages.internal.sentMessages;

import interfaces.visitors.InternalMessageVisitor;
import interfaces.visitors.InternalSentMessageVisitor;

public class InternalExitMessage extends InternalSentMessage {

    // ***************************************************************************************************************
    // Factory pattern for InternalExitMessage

    protected static InternalExitMessage build(Class<?> clazz) {
        return new InternalExitMessage(clazz);
    }

    private InternalExitMessage(Class<?> clazz) {
        this.clazz = clazz;
    }

    // ***************************************************************************************************************
    // Visitor pattern for InternalExitMessage

    @Override
    public void accept(InternalMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(InternalSentMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ***************************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return "(%s) EXIT".formatted(clazz.getClass().getSimpleName());
    }
}
