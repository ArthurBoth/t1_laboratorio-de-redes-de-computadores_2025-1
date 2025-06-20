package messages.internal.received;

import static utils.Constants.Strings.ACK_FORMAT;
import static utils.Constants.Strings.ACK_LOG_FORMAT;

import interfaces.visitors.LoggerVisitor;
import interfaces.visitors.internal.InternalMessageVisitor;
import interfaces.visitors.internal.InternalReceivedMessageVisitor;

public class InternalReceivedAckMessage extends InternalReceivedMessage {
    private int messageId;

    public int getAcknowledgedId() {
        return messageId;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalReceivedAckMessage

    @Override
    public void accept(InternalReceivedMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(LoggerVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(InternalMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return ACK_FORMAT.formatted(
            messageId
        );
    }

    @Override
    public String getPrettyMessage() {
        return ACK_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            sourceIp.getHostAddress(),
            messageId
        );
    }

    // ****************************************************************************************************
    // Builder pattern for InternalReceivedAckMessage

    public static IpSetter<InternalReceivedAckMessage> ack(Class<?> clazz, int messageId) {
        return new Builder(clazz, messageId);
    }

    private static class Builder extends IpBuilder<InternalReceivedAckMessage> {
        private Class<?> clazz;
        private int messageId;

        private Builder(Class<?> clazz, int messageId) {
            this.clazz     = clazz;
            this.messageId = messageId;
        }

        @Override
        protected InternalReceivedAckMessage self() {
            return new InternalReceivedAckMessage(this);
        }
    }

    private InternalReceivedAckMessage(Builder builder) {
        this.clazz     = builder.clazz;
        this.sourceIp  = builder.sourceIp;
        this.messageId = builder.messageId;
        this.port      = builder.port;
    }
}
