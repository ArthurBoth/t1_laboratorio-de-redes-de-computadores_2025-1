package messages.internal.requested;

import static utils.Constants.Strings.ABORT_FILE_REQUEST;
import static utils.Constants.Strings.ABORT_FILE_SEND_LOG_FORMAT;

import interfaces.visitors.LoggerVisitor;
import interfaces.visitors.internal.InternalRequestMessageVisitor;

public class InternalRequestAbortFileSendingMessage extends InternalRequestMessage {
    
    // ****************************************************************************************************
    // Factory pattern for InternalRequestAbortFileSendingMessage

    protected static InternalRequestAbortFileSendingMessage build(Class<?> clazz) {
        return new InternalRequestAbortFileSendingMessage(clazz);
    }

    private InternalRequestAbortFileSendingMessage(Class<?> clazz) {
        this.clazz = clazz;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalRequestAbortFileSendingMessage

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
        return ABORT_FILE_REQUEST;
    }

    @Override
    public String getPrettyMessage() {
        return ABORT_FILE_SEND_LOG_FORMAT.formatted(
            clazz.getSimpleName()
        );
    }
}
