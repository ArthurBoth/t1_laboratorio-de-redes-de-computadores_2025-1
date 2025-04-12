package network.threads.messages;

import constants.Constants.Strings;

public class AckMessage extends ExternalMessage {
    private final int ACK_ID;

    protected AckMessage(AckMessageBuilder builder) {
        super(builder);
        ACK_ID = builder.getAckId();
    }

    @Override
    public byte[] getMessageBytes() {
        String message = String.format(Strings.ACK_FORMAT, ACK_ID);
        return message.getBytes();
    }   
}
