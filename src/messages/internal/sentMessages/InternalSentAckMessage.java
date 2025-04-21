package messages.internal.sentMessages;

import interfaces.visitors.InternalMessageVisitor;
import interfaces.visitors.InternalSentMessageVisitor;

import static utils.Constants.Strings.ACK_SENDING_REQUEST_FORMAT;

public class InternalSentAckMessage extends InternalSentMessage {
    private int ackkedId;

    public int getAcknowledgedMessageId() {
        return ackkedId;
    }

    // **************************************************************************************************************
    // Visitor pattern for InternalSentAckMessage

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
        return ACK_SENDING_REQUEST_FORMAT.formatted(
            clazz.getSimpleName(), 
            ackkedId
            );
    }

    // **************************************************************************************************************
    // Builder pattern for InternalSentAckMessage

    public static IpSetter<InternalSentAckMessage> create(Class<?> clazz, int ackkedId) {
        return new Builder(clazz, ackkedId);
    }

    private static class Builder extends IpBuilder<InternalSentAckMessage> {
        private Class<?> clazz;
        private int ackkedId;

        private Builder(Class<?> clazz, int ackkedId) {
            this.clazz    = clazz;
            this.ackkedId = ackkedId;
        }

        @Override
        public InternalSentAckMessage self() {
            return new InternalSentAckMessage(this);
        }
    }

    private InternalSentAckMessage(Builder builder) {
        this.clazz         = builder.clazz;
        this.destinationIp = builder.destinationIp;
        this.ackkedId      = builder.ackkedId;
    }
}
