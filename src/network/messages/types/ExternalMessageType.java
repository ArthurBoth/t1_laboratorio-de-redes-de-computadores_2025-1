package network.messages.types;

import static constants.Constants.MessageHeaders.ACK_HEADER;
import static constants.Constants.MessageHeaders.CHUNK_HEADER;
import static constants.Constants.MessageHeaders.END_HEADER;
import static constants.Constants.MessageHeaders.FILE_HEADER;
import static constants.Constants.MessageHeaders.HEARTBEAT_HEADER;
import static constants.Constants.MessageHeaders.NACK_HEADER;
import static constants.Constants.MessageHeaders.TALK_HEADER;

public enum ExternalMessageType {
    HEARTBEAT, TALK, FILE, CHUNK, END, ACK, NACK;

    public static ExternalMessageType decodeHeader(byte header) {
        return switch ((char) header) {
            case HEARTBEAT_HEADER -> HEARTBEAT;
            case TALK_HEADER      -> TALK;
            case FILE_HEADER      -> FILE;
            case CHUNK_HEADER     -> CHUNK;
            case END_HEADER       -> END;
            case ACK_HEADER       -> ACK;
            case NACK_HEADER      -> NACK;
            default               -> null;
        };
    }
}
