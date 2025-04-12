package network.threads.messages;

import constants.Constants.Strings;

public class NAckMessage extends ExternalMessage {
    private final int NACK_ID;

    protected NAckMessage(NAckMessageBuilder builder) {
        super(builder);
        NACK_ID = builder.getNackId();
    }

    @Override
    public byte[] getMessageBytes() {
        String message = String.format(Strings.NACK_FORMAT, NACK_ID);
        return message.getBytes();
    }    
}
