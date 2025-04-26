package messages.foreign;

import static utils.Constants.Configs.BROADCAST_IP;

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

    public static ForeignHeartbeatMessage create() {
        return new ForeignHeartbeatMessage();
    }

    private ForeignHeartbeatMessage() {
        this.destinationIp = BROADCAST_IP;
    }
}
