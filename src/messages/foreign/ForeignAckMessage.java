package messages.foreign;

import interfaces.visitors.EncoderVisitor;

public class ForeignAckMessage extends ForeignMessage {
    private final int ACKKED_MESSAGE_ID;

    public int getAckkedId() {
        return ACKKED_MESSAGE_ID;
    }

    // ****************************************************************************************************
    // Visitor pattern for ForeignAckMessage

    @Override
    public byte[] encode(EncoderVisitor visitor) {
        return visitor.encode(this);
    }
    
    // ****************************************************************************************************
    // Builder pattern for ForeignAckMessage

    public static IpSetter<ForeignAckMessage> create(Class<?> clazz, int ackkedMessageId) {
        return new Builder(clazz, ackkedMessageId);
    }

    private static class Builder extends IpBuilder<ForeignAckMessage> {
        private final int ACKKED_MESSAGE_ID;
        private Class<?> clazz;

        private Builder(Class<?> clazz, int ackkedMessageId) {
            this.ACKKED_MESSAGE_ID = ackkedMessageId;
            this.clazz             = clazz;
        }

        @Override
        protected ForeignAckMessage self() {
            return new ForeignAckMessage(this);
        }
    }

    private ForeignAckMessage(Builder builder) {
        this.ACKKED_MESSAGE_ID = builder.ACKKED_MESSAGE_ID;
        this.clazz             = builder.clazz;
        this.destinationIp     = builder.destinationIp;
    }
}
