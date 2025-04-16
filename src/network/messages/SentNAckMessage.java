package network.messages;

import constants.Constants.Strings;

public class SentNAckMessage extends ExternalMessage {
    private final int NACK_ID;
    private final String REASON;

    protected SentNAckMessage(SentNAckMessageBuilder builder) {
        super(builder);
        NACK_ID = builder.getNackId();
        REASON  = builder.getReason();
    }

    @Override
    public byte[] getMessageBytes() {
        String message = String.format(Strings.NACK_FORMAT, NACK_ID, REASON);
        return message.getBytes();
    }    
}
