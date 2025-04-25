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

    public static IpBuilder<ForeignAckMessage> create(int ackkedMessageId) {
        return new Builder(ackkedMessageId);
    }

    private static class Builder extends IpBuilder<ForeignAckMessage> {
        private final int ACKKED_MESSAGE_ID;

        private Builder(int ackkedMessageId) {
            this.ACKKED_MESSAGE_ID = ackkedMessageId;
        }

        @Override
        protected ForeignAckMessage self() {
            return new ForeignAckMessage(this);
        }
    }

    private ForeignAckMessage(Builder builder) {
        this.ACKKED_MESSAGE_ID = builder.ACKKED_MESSAGE_ID;
        this.destinationIp     = builder.destinationIp;
    }
}
