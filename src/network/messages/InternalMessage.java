package network.messages;

import network.messages.types.InternalMessageType;

public class InternalMessage extends ThreadMessage {
    private final InternalMessageType TYPE;

    protected InternalMessage(InternalMessageBuilder builder) {
        this.TYPE = builder.getType();
    }
    
    public final boolean isExternalMessage() {
        return false;
    }

    public final InternalMessageType getType() {
        return TYPE;
    }
}
