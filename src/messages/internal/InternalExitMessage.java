package messages.internal;

import interfaces.visitors.InternalMessageVisitor;

public class InternalExitMessage extends InternalMessage {

    // ***************************************************************************************************************
    // Factory pattern for InternalExitMessage

    protected static InternalExitMessage create(Class<?> clazz) {
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

    // ***************************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return "(%s) EXIT".formatted(clazz.getClass().getSimpleName());
    }
}
