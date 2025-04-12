package network.threads.messages;

import constants.Constants.Strings;

public class ExternalMessage extends ThreadMessage {
    private final ExternalMessageType TYPE; 
    private final String DESTINATION_IP;

    protected final int ID;
    protected final String STRING_FIELD;

    protected ExternalMessage(ExternalMessageBuilder builder) {
        TYPE           = builder.getType();
        DESTINATION_IP = builder.getDestinationIp();
        ID             = builder.getId();
        STRING_FIELD   = builder.getStringField();
    }

    @Override
    public MessageType getType() {
        return TYPE.getMessageType();
    }

    public String getDestinationIp() {
        return DESTINATION_IP;
    }

    public byte[] getMessageBytes() {
        switch (TYPE) {
            case HEARTBEAT -> {return assembleHeartbeatMessage().getBytes();}
            case TALK      -> {return assembleTalkMessage().getBytes();}
            case END       -> {return assembleEndMessage().getBytes();}
            default        -> {throw new IllegalArgumentException("Invalid external message type: " + TYPE);}
        }
    }

    public String getMessage() {
        switch (TYPE) {
            case HEARTBEAT -> {return assembleHeartbeatMessage();}
            case TALK      -> {return assembleTalkMessage();}
            case END       -> {return assembleEndMessage();}
            default        -> {throw new IllegalArgumentException("Invalid external message type: " + TYPE);}
        }
    }

    private String assembleHeartbeatMessage() {
        return Strings.HEARTBEAT_MESSAGE;
    }

    private String assembleTalkMessage() {
        return String.format(Strings.TALK_FORMAT, ID, STRING_FIELD);
    }

    private String assembleEndMessage() {
        return String.format(Strings.END_FORMAT, ID, STRING_FIELD);
    }
}
