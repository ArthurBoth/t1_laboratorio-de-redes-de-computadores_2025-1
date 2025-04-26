package messages.internal.requested;

import static utils.Constants.Strings.RESEND_FORMAT;
import static utils.Constants.Strings.RESEND_REQUEST_LOG_FORMAT;

import interfaces.visitors.internal.InternalRequestMessageVisitor;

public class InternalRequestResendMessage extends InternalRequestMessage {
    private int resendMessageId;

    public int getResendId() {
        return resendMessageId;
    }

    // ****************************************************************************************************
    // Factory pattern for InternalSentNAckMessage

    public static InternalRequestResendMessage build(Class<?> clazz, int resendMessageId) {
        return new InternalRequestResendMessage(clazz, resendMessageId);
    }
    private InternalRequestResendMessage(Class<?> clazz, int resendMessageId) {
        this.resendMessageId = resendMessageId;
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
        return RESEND_FORMAT.formatted(resendMessageId);
    }

    @Override
    public String getPrettyMessage() {
        return RESEND_REQUEST_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            resendMessageId
        );
    }
}
