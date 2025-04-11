package network.threads.messages;

public enum MessageType {
    
    // **************************************************************************
    // Internal Messages
    EXIT, SEND_FILE,

    // **************************************************************************
    // External Messages
    HEARTBEAT, TALK, FILE, CHUNK, END, ACK, NACK;
}
