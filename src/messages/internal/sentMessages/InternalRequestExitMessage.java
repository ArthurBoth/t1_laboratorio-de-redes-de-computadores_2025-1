package messages.internal.sentMessages;

import static utils.Constants.Strings.EXIT_MESSAGE;

import interfaces.visitors.InternalMessageVisitor;
import messages.internal.InternalMessage;

public class InternalRequestExitMessage extends InternalMessage {

    // ****************************************************************************************************
    // Factory pattern for InternalExitMessage

    protected static InternalRequestExitMessage build(Class<?> clazz) {
        return new InternalRequestExitMessage(clazz);
    }

    private InternalRequestExitMessage(Class<?> clazz) {
        this.clazz = clazz;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalExitMessage

    @Override
    public void accept(InternalMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return EXIT_MESSAGE;
    }
}
