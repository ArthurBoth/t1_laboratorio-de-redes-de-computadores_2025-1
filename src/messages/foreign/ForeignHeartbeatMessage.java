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

    public static IpSetter<ForeignHeartbeatMessage> create(Class<?> clazz) {
        return new Builder(clazz);
    }

    private static class Builder extends IpBuilder<ForeignHeartbeatMessage> {
        private Class<?> clazz;

        private Builder (Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        protected ForeignHeartbeatMessage self() {
            return new ForeignHeartbeatMessage(this);
        }
    }

    private ForeignHeartbeatMessage(Builder builder) {
        this.clazz            = builder.clazz;
        this.destinationIp    = builder.destinationIp;
    }
}
