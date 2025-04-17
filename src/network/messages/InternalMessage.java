package network.messages;

import network.messages.types.InternalMessageType;

public class InternalMessage extends ThreadMessage {
    private final InternalMessageType TYPE;
    private final String STRING_FIELD;

    protected InternalMessage(InternalMessageBuilder builder) {
        this.TYPE         = builder.getType();
        this.STRING_FIELD = builder.getStringField();
    }

    public final InternalMessageType getType() {
        return TYPE;
    }

    public String getString() {
        return STRING_FIELD;
    }

    @Override
    public boolean accept(ThreadMessageVisitor visitor) {
        return visitor.process(this);
    }
}
