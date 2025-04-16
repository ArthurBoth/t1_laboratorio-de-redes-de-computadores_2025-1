package network.messages;

public class ReceivedNAckMessage extends ReceivedMessage {
    private final int NACK_ID;
    private final String REASON;

    protected ReceivedNAckMessage(ReceivedNAckMessageBuilder builder) {
        super(builder);
        NACK_ID = builder.getNackId();
        REASON  = builder.getReason();
    }

    public int getNackId() {
        return NACK_ID;
    }
    
    public String getReason() {
        return REASON;
    }
}
