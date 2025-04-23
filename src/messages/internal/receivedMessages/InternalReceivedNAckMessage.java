package messages.internal.receivedMessages;

import static utils.Constants.Strings.NACK_FORMAT;
import static utils.Constants.Strings.NACK_LOG_FORMAT;

import interfaces.visitors.InternalMessageVisitor;

public class InternalReceivedNAckMessage extends InternalReceivedMessage {
    private int messageId;
    private String reason;

    public int getNonAcknowledgedId() {
        return messageId;
    }

    public String getReasson() {
        return reason;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalReceivedNAckMessage

    @Override
    public void accept(InternalMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return NACK_FORMAT.formatted(
            messageId,
            reason
        );
    }

    @Override
    public String getPrettyMessage() {
        return NACK_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            sourceIp.getHostAddress(),
            messageId,
            reason
        );
    }

    // ****************************************************************************************************
    // Builder pattern for InternalReceivedNAckMessage

    public static StringSetter nAck(Class<?> clazz, int messageId) {
        return new Builder(clazz, messageId);
    }

    public interface StringSetter {
        IpSetter<InternalReceivedNAckMessage> reason(String reason);
    }

    private static class Builder extends IpBuilder<InternalReceivedNAckMessage> implements StringSetter{
        private Class<?> clazz;
        private int messageId;
        private String reason;

        private Builder(Class<?> clazz, int messageId) {
            this.clazz     = clazz;
            this.messageId = messageId;
        }

        @Override
        public IpSetter<InternalReceivedNAckMessage> reason(String reason) {
            this.reason = reason;
            return this;
        }

        @Override
        protected InternalReceivedNAckMessage self() {
            return new InternalReceivedNAckMessage(this);
        }
    }

    private InternalReceivedNAckMessage(Builder builder) {
        this.clazz     = builder.clazz;
        this.sourceIp  = builder.sourceIp;
        this.reason    = builder.reason;
        this.messageId = builder.messageId;
    }
}
