package network.messages.types;

public enum InternalMessageType {
    // Message types for internal communication
    EXIT, 
    SEND_MESSAGE,
    SEND_FILE,

    // Message types for received messages
    RECEIVED_HEARTBEAT, 
    RECEIVED_TALK, 
    RECEIVED_FILE, 
    RECEIVED_CHUNK,
    RECEIVED_END,
    RECEIVED_ACK,
    RECEIVED_NACK
}
