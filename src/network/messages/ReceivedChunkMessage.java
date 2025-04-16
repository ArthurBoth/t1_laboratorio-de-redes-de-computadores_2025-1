package network.messages;

public class ReceivedChunkMessage extends ReceivedMessage {
    private final int SEQUENCE_NUMBER;
    private final byte[] DATA;

    public ReceivedChunkMessage(ReceivedChunkMessageBuilder builder) {
        super(builder);
        this.SEQUENCE_NUMBER = builder.getSequenceNumber();
        this.DATA            = builder.getData();
    }

    public int getChunkId() {
        return SEQUENCE_NUMBER;
    }

    public byte[] getChunkData() {
        return DATA;
    }
    
}
