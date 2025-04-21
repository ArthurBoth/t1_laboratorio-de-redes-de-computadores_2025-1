package messages.internal.sentMessages;

import interfaces.visitors.InternalMessageVisitor;
import interfaces.visitors.InternalSentMessageVisitor;

import static utils.Constants.Strings.NACK_SENDING_REQUEST_FORMAT;

public class InternalSentNAckMessage extends InternalSentMessage {
    private int nonAckkedId;
    private String reason;

    public int getNonAcknowledgedMessageId() {
        return nonAckkedId;
    }

    public String getReason() {
        return reason;
    }

    // **************************************************************************************************************
    // Visitor pattern for InternalSentNAckMessage

    @Override
    public void accept(InternalMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(InternalSentMessageVisitor visitor) {
        visitor.visit(this);
    }

    // **************************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return NACK_SENDING_REQUEST_FORMAT.formatted(
            clazz.getSimpleName(), 
            nonAckkedId
            );
    }

    // **************************************************************************************************************
    // Builder pattern for InternalSentNAckMessage

    public static StringSetter create(Class<?> clazz, int nonAckkedId) {
        return new Builder(clazz, nonAckkedId);
    }

    public interface StringSetter {
        IpSetter<InternalSentNAckMessage> because(String reason);
    }

    private static class Builder extends IpBuilder<InternalSentNAckMessage> implements StringSetter {
        private Class<?> clazz;
        private int nonAckkedId;
        private String reason;

        private Builder(Class<?> clazz, int nonAckkedId) {
            this.clazz       = clazz;
            this.nonAckkedId = nonAckkedId;
        }

        @Override
        public IpSetter<InternalSentNAckMessage> because(String reason) {
            this.reason = reason;
            return this;
        }

        @Override
        public InternalSentNAckMessage self() {
            return new InternalSentNAckMessage(this);
        }
    }

    private InternalSentNAckMessage(Builder builder) {
        this.clazz         = builder.clazz;
        this.destinationIp = builder.destinationIp;
        this.nonAckkedId   = builder.nonAckkedId;
        this.reason        = builder.reason;
    }
}
