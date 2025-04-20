package messages.internal.acknowledgments;

import interfaces.visitors.InternalMessageVisitor;

import static utils.Constants.Strings.NACK_LOG_FORMAT;

public class NAckMessage extends AcknowledgmentMessage  {
    private String reasson;

    public int getNonAcknowledgedMessageId() {
        return this.messageId;
    }

    public String getReasson() {
        return reasson;
    }
    
    // **************************************************************************************************************
    // Visitor pattern for NAckMessage
    
    @Override
    public void accept(InternalMessageVisitor visitor) {
        visitor.visit(this);
    }

    // **************************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return NACK_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            sourceIp.getHostAddress(), 
            messageId
        );
    }

    // **************************************************************************************************************
    // Builder pattern for NAckMessage

    public static IpSetter<NAckMessage> create(Class<?> clazz, int messageId) {
        return new Builder(clazz, messageId);
    }

    public interface StringSetter {
        IpSetter<NAckMessage> reason(String reasson);
    }

    private static class Builder extends IpBuilder<NAckMessage> implements StringSetter {
        private Class<?> clazz;
        private int messageId;
        private String reasson;

        private Builder(Class<?> clazz, int messageId) {
            this.clazz     = clazz;
            this.messageId = messageId;
        }

        @Override
        public IpSetter<NAckMessage> reason(String reasson) {
            this.reasson = reasson;
            return this;
        }

        @Override
        protected NAckMessage self() {
            return new NAckMessage(this);
        }
    }

    private NAckMessage(Builder builder) {
        this.clazz     = builder.clazz;
        this.messageId = builder.messageId;
        this.sourceIp  = builder.sourceIp;
        this.reasson   = builder.reasson;
    }
}
