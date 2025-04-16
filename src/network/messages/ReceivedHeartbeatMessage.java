package network.messages;

public class ReceivedHeartbeatMessage extends ReceivedMessage {

    protected ReceivedHeartbeatMessage(ReceivedMessageBuilder builder) {
        super(builder);
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException("Heartbeat does not have an internal Id");
    }
}
