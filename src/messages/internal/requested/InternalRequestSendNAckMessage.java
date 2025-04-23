package messages.internal.requested;

import static utils.Constants.Strings.NACK_FORMAT;
import static utils.Constants.Strings.NACK_SENDING_LOG_FORMAT;

import interfaces.visitors.internal.InternalRequestMessageVisitor;

public class InternalRequestSendNAckMessage extends InternalRequestSendMessage {
    private int nonAckkedId;
    private String reason;

    public int getNonAcknowledgedMessageId() {
        return nonAckkedId;
    }

    public String getReason() {
        return reason;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalSentNAckMessage

    @Override
    public void accept(InternalRequestMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return NACK_FORMAT.formatted(
            nonAckkedId,
            reason
            );
    }

    @Override
    public String getPrettyMessage() {
        return NACK_SENDING_LOG_FORMAT.formatted(
            clazz.getSimpleName(), 
            nonAckkedId,
            reason
            );
    }

    // ****************************************************************************************************
    // Builder pattern for InternalSentNAckMessage

    public static StringSetter create(Class<?> clazz, int nonAckkedId) {
        return new Builder(clazz, nonAckkedId);
    }

    public interface StringSetter {
        IpSetter<InternalRequestSendNAckMessage> because(String reason);
    }

    private static class Builder extends IpBuilder<InternalRequestSendNAckMessage> implements StringSetter {
        private Class<?> clazz;
        private int nonAckkedId;
        private String reason;

        private Builder(Class<?> clazz, int nonAckkedId) {
            this.clazz       = clazz;
            this.nonAckkedId = nonAckkedId;
        }

        @Override
        public IpSetter<InternalRequestSendNAckMessage> because(String reason) {
            this.reason = reason;
            return this;
        }

        @Override
        public InternalRequestSendNAckMessage self() {
            return new InternalRequestSendNAckMessage(this);
        }
    }

    private InternalRequestSendNAckMessage(Builder builder) {
        this.clazz         = builder.clazz;
        this.destinationIp = builder.destinationIp;
        this.nonAckkedId   = builder.nonAckkedId;
        this.reason        = builder.reason;
    }
}
