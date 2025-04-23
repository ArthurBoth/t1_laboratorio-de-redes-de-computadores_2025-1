package messages.internal.requested;

import static utils.Constants.Strings.ACK_FORMAT;
import static utils.Constants.Strings.ACK_SENDING_LOG_FORMAT;

import interfaces.visitors.internal.InternalRequestMessageVisitor;

public class InternalRequestSendAckMessage extends InternalRequestSendMessage {
    private int ackkedId;

    public int getAcknowledgedMessageId() {
        return ackkedId;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalSentAckMessage

    @Override
    public void accept(InternalRequestMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return ACK_FORMAT.formatted(
            ackkedId
            );
    }

    @Override
    public String getPrettyMessage() {
        return ACK_SENDING_LOG_FORMAT.formatted(
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
