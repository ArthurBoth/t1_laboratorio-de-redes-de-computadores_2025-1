package messages.internal.sentMessages;

import interfaces.visitors.InternalMessageVisitor;
import interfaces.visitors.InternalSentMessageVisitor;

import static utils.Constants.Strings.ACK_SENDING_REQUEST_FORMAT;

public class InternalRequestSendAckMessage extends InternalRequestMessage {
    private int ackkedId;

    public int getAcknowledgedMessageId() {
        return ackkedId;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalSentAckMessage

    @Override
    public void accept(InternalMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(InternalSentMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return ACK_SENDING_REQUEST_FORMAT.formatted(
            clazz.getSimpleName(), 
            ackkedId
            );
    }

    // ****************************************************************************************************
    // Builder pattern for InternalSentAckMessage

    public static IpSetter<InternalRequestSendAckMessage> create(Class<?> clazz, int ackkedId) {
        return new Builder(clazz, ackkedId);
    }

    private static class Builder extends IpBuilder<InternalRequestSendAckMessage> {
        private Class<?> clazz;
        private int ackkedId;

        private Builder(Class<?> clazz, int ackkedId) {
            this.clazz    = clazz;
            this.ackkedId = ackkedId;
        }

        @Override
        public InternalRequestSendAckMessage self() {
            return new InternalRequestSendAckMessage(this);
        }
    }

    private InternalRequestSendAckMessage(Builder builder) {
        this.clazz         = builder.clazz;
        this.destinationIp = builder.destinationIp;
        this.ackkedId      = builder.ackkedId;
    }
}
