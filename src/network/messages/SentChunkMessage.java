package network.messages;

import constants.Constants.Strings;

public class SentChunkMessage extends ExternalMessage {
    private final int SEQUENCE_NUMBER;
    private final byte[] DATA;

    protected SentChunkMessage(SentChunkMessageBuilder builder) {
        super(builder);
        SEQUENCE_NUMBER = builder.getSequenceNumber();
        DATA            = builder.getData();
    }

    @Override
    public byte[] getMessageBytes() {
        int resultLegth;
        String message;
        byte[] result;
        byte[] messageBytes;
        
        message      = String.format(Strings.CHUNK_FORMAT, ID, SEQUENCE_NUMBER);
        messageBytes = message.getBytes();
        resultLegth  = messageBytes.length + DATA.length;
        result       = new byte[resultLegth];

        System.arraycopy(messageBytes, 0, result, 0, messageBytes.length);
        System.arraycopy(DATA, 0, result, messageBytes.length, DATA.length);
        
        return result;
    }
}
