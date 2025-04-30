package messages.internal.requested;

import static utils.Constants.Strings.DISPLAY_SUCCESS_LOG_FORMAT;
import static utils.Constants.Strings.DISPLAY_SUCCESS_REQUEST;

import interfaces.visitors.LoggerVisitor;
import interfaces.visitors.internal.InternalRequestMessageVisitor;

public class InternalRequestDisplaySuccessMessage extends InternalRequestMessage {

    // ****************************************************************************************************
    // Factory pattern for InternalRequestDisplaySuccessMessage

    protected static InternalRequestDisplaySuccessMessage build(Class<?> clazz) {
        return new InternalRequestDisplaySuccessMessage(clazz);
    }

    private InternalRequestDisplaySuccessMessage(Class<?> clazz) {
        this.clazz = clazz;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalRequestDisplaySuccessMessage

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
        return DISPLAY_SUCCESS_REQUEST;
    }

    @Override
    public String getPrettyMessage() {
        return DISPLAY_SUCCESS_LOG_FORMAT.formatted(
            clazz.getSimpleName()
        );
    }
}
