package network.messages;

public class ReceivedAckMessage extends ReceivedMessage {
    private final int ACK_ID;

    protected ReceivedAckMessage(ReceivedAckMessageBuilder builder) {
        super(builder);
        this.ACK_ID = builder.getAckId();
    }

    public int getAckId() {
        return ACK_ID;
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException("Ack does not have an internal Id, use getAckId() to get the Id of the Acknowledged message");
    }
}
