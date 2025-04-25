package messages.foreign;

import interfaces.visitors.EncoderVisitor;

public class ForeignHeartbeatMessage extends ForeignMessage {

    // ****************************************************************************************************
    // Visitor pattern for ForeignHeartbeatMessage

    @Override
    public byte[] encode(EncoderVisitor visitor) {
        return visitor.encode(this);
    }

    // ****************************************************************************************************
    // Builder pattern for ForeignHeartbeatMessage

    public static IpBuilder<ForeignHeartbeatMessage> create() {
        return new Builder();
    }

    private static class Builder extends IpBuilder<ForeignHeartbeatMessage> {

        @Override
        protected ForeignHeartbeatMessage self() {
            return new ForeignHeartbeatMessage(this);
        }
    }

    private ForeignHeartbeatMessage(Builder builder) {
        this.destinationIp = builder.destinationIp;
    }
}
