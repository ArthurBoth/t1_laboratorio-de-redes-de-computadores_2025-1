package messages.internal.requested;

import static utils.Constants.Strings.EXIT_MESSAGE;
import static utils.Constants.Strings.EXIT_SENDING_LOG_FORMAT;

import interfaces.visitors.internal.InternalRequestMessageVisitor;

public class InternalRequestExitMessage extends InternalRequestMessage {

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
    public void accept(InternalRequestMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return EXIT_MESSAGE;
    }

    @Override
    public String getPrettyMessage() {
        return EXIT_SENDING_LOG_FORMAT.formatted(clazz.getSimpleName());
    }
}
