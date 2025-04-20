package messages.foreign;

import interfaces.visitors.ForeignMessageVisitor;

import static utils.Constants.Strings.NACK_FORMAT;

public class ForeignNAckMessage extends ForeignMessage {
    private final int NON_ACKKED_MESSAGE_ID;
    private String reason;

    // **************************************************************************************************************
    // Inherited fields from ForeignMessage

    @Override
    protected String assembleFormattedMessage() {
        return NACK_FORMAT.formatted(
            NON_ACKKED_MESSAGE_ID,
            reason
            );
    }    

    // **************************************************************************************************************
    // Visitor pattern for ForeignNAckMessage

    @Override
    public void accept(ForeignMessageVisitor visitor) {
        visitor.visit(this);
    }

    // **************************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return "(%s) %s".formatted(
            clazz.getSimpleName(),
            formattedMessage
            );
    }
    
    // **************************************************************************************************************
    // Builder pattern for ForeignNAckMessage

    public static StringSetter create(Class<?> clazz, int nonAckkedMessageId) {
        return new Builder(clazz, nonAckkedMessageId);
    }

    public interface StringSetter {
        IpSetter<ForeignNAckMessage> because(String reason);
    }

    private static class Builder extends IpBuilder<ForeignNAckMessage> implements StringSetter {
        private final int NON_ACKKED_MESSAGE_ID;
        private Class<?> clazz;
        private String reason;

        private Builder(Class<?> clazz, int nonAckkedMessageId) {
            this.NON_ACKKED_MESSAGE_ID = nonAckkedMessageId;
            this.clazz                 = clazz;
        }

        @Override
        public IpSetter<ForeignNAckMessage> because(String reason) {
            this.reason = reason;
            return this;
        }

        @Override
        protected ForeignNAckMessage self() {
            return new ForeignNAckMessage(this);
        }
    }

    private ForeignNAckMessage(Builder builder) {
        this.NON_ACKKED_MESSAGE_ID = builder.NON_ACKKED_MESSAGE_ID;
        this.clazz                 = builder.clazz;
        this.destinationIp         = builder.destinationIp;
        this.reason                = builder.reason;
    }
}
