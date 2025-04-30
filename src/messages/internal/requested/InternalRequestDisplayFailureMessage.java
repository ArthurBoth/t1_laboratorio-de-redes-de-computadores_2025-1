package messages.internal.requested;

import static utils.Constants.Strings.DISPLAY_FAILURE_LOG_FORMAT;
import static utils.Constants.Strings.DISPLAY_FAILURE_REQUEST;

import interfaces.visitors.LoggerVisitor;
import interfaces.visitors.internal.InternalRequestMessageVisitor;

public class InternalRequestDisplayFailureMessage extends InternalRequestMessage {

    // ****************************************************************************************************
    // Factory pattern for InternalRequestDisplayFailureMessage

    protected static InternalRequestDisplayFailureMessage build(Class<?> clazz) {
        return new InternalRequestDisplayFailureMessage(clazz);
    }

    private InternalRequestDisplayFailureMessage(Class<?> clazz) {
        this.clazz = clazz;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalRequestDisplayFailureMessage

    @Override
    public void accept(InternalRequestMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(LoggerVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return DISPLAY_FAILURE_REQUEST;
    }

    @Override
    public String getPrettyMessage() {
        return DISPLAY_FAILURE_LOG_FORMAT.formatted(
            clazz.getSimpleName()
        );
    }
    
}
