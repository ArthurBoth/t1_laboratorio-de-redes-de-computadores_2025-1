package messages.foreign;

import interfaces.visitors.EncoderVisitor;
import interfaces.visitors.ForeignMessageVisitor;

import static utils.Constants.Strings.NACK_FORMAT;
import static utils.Constants.Strings.NACK_LOG_FORMAT;

public class ForeignNAckMessage extends ForeignMessage {
    private final int NON_ACKKED_MESSAGE_ID;
    private String reason;

    public int getNonAckkedId() {
        return this.NON_ACKKED_MESSAGE_ID;
    }

    public String getReason() {
        return this.reason;
    }

    // **************************************************************************************************************
    // Visitor pattern for ForeignNAckMessage

    @Override
    public void accept(ForeignMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public byte[] encode(EncoderVisitor visitor) {
        return visitor.encode(this);
    }

    // **************************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return NACK_FORMAT.formatted(
            NON_ACKKED_MESSAGE_ID,
            reason
            );
    }

    @Override
    public String getPrettyMessage() {
        return NACK_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            destinationIp.getHostAddress(),
            NON_ACKKED_MESSAGE_ID,
            reason
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
