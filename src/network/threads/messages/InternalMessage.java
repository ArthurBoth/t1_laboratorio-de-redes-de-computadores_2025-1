package network.threads.messages;

public class InternalMessage extends ThreadMessage {
    private MessageType type;

    public InternalMessage(InternalMessageBuilder builder) {
        this.type = builder.getType();
    }

    @Override
    public MessageType getType() {
        return type;
    }
}
