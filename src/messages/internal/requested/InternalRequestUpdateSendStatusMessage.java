package messages.internal.requested;

import static utils.Constants.Strings.UPDATE_STATUS_LOG_FORMAT;
import static utils.Constants.Strings.UPDATE_STATUS_MESSAGE;

import interfaces.visitors.LoggerVisitor;
import interfaces.visitors.internal.InternalRequestMessageVisitor;

public class InternalRequestUpdateSendStatusMessage extends InternalRequestMessage {
    long size;

    public long getSize() {
        return size;
    }
    
    // ****************************************************************************************************
    // Factory pattern for InternalRequestUpdateSendStatusMessage

    protected static InternalRequestUpdateSendStatusMessage build(Class<?> clazz, long size) {
        return new InternalRequestUpdateSendStatusMessage(clazz, size);
    }

    private InternalRequestUpdateSendStatusMessage(Class<?> clazz, long size) {
        this.clazz = clazz;
        this.size  = size;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalRequestUpdateSendStatusMessage

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
        return UPDATE_STATUS_MESSAGE;
    }

    @Override
    public String getPrettyMessage() {
        return UPDATE_STATUS_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            size
        );
    }
}
