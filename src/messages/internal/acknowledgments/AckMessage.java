package messages.internal.acknowledgments;

import interfaces.visitors.InternalMessageVisitor;

import static utils.Constants.Strings.ACK_LOG_FORMAT;;

public class AckMessage extends AcknowledgmentMessage {

    public int getAcknowledgedMessageId() {
        return this.messageId;
    }
    
    // **************************************************************************************************************
    // Visitor pattern for AckMessage
    
    @Override
    public void accept(InternalMessageVisitor visitor) {
        visitor.visit(this);
    }

    // **************************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return ACK_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            sourceIp.getHostAddress(), 
            messageId
        );
    }

    // **************************************************************************************************************
    // Builder pattern for AckMessage

    public static IpSetter<AckMessage> create(Class<?> clazz, int messageId) {
        return new Builder(clazz, messageId);
    }

    private static class Builder extends IpBuilder<AckMessage> {
        private Class<?> clazz;
        private int messageId;

        private Builder(Class<?> clazz, int messageId) {
            this.clazz     = clazz;
            this.messageId = messageId;
        }

        @Override
        protected AckMessage self() {
            return new AckMessage(this);
        }
    }

    private AckMessage(Builder builder) {
        this.clazz     = builder.clazz;
        this.messageId = builder.messageId;
        this.sourceIp  = builder.sourceIp;
    }
}
