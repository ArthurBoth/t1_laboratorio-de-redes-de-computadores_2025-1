package messages.foreign;

import interfaces.visitors.EncoderVisitor;
import interfaces.visitors.MessageVisitor;

public class ForeignTalkMessage extends ForeignMessage {
    private final int MESSAGE_ID;
    private String messageContent;

    public int getMessageId() {
        return this.MESSAGE_ID;
    }

    public String getContent() {
        return this.messageContent;
    }

    @Override
    public void accept(MessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public byte[] encode(EncoderVisitor visitor) {
        return visitor.encode(this);
    }

    // ****************************************************************************************************
    // Builder pattern for ForeignTalkMessage

    public static IpSetter<ForeignTalkMessage> create(Class<?> clazz, int messageId, String content) {
        return new Builder(clazz, messageId, content);
    }

    private static class Builder extends IpBuilder<ForeignTalkMessage> {
        private final int MESSAGE_ID;
        private Class<?> clazz;
        private String messageContent;

        private Builder(Class<?> clazz, int messageId, String messageContent) {
            this.MESSAGE_ID     = messageId;
            this.clazz          = clazz;
            this.messageContent = messageContent;
        }

        @Override
        protected ForeignTalkMessage self() {
            return new ForeignTalkMessage(this);
        }
    }

    private ForeignTalkMessage(Builder builder) {
        this.MESSAGE_ID       = builder.MESSAGE_ID;
        this.clazz            = builder.clazz;
        this.destinationIp    = builder.destinationIp;
        this.messageContent   = builder.messageContent;
    }
}
