package messages.internal.receivedMessages;

import interfaces.visitors.InternalMessageVisitor;

import static utils.Constants.Strings.TALK_LOG_FORMAT;

public class InternalReceivedTalkMessage extends InternalReceivedMessage {
    private String content;

    public String getContent() {
        return content;
    }

    // **************************************************************************************************************
    // Visitor pattern for InternalReceivedTalkMessage

    @Override
    public void accept(InternalMessageVisitor visitor) {
        visitor.visit(this);
    }

    // **************************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return TALK_LOG_FORMAT.formatted(
            clazz.getSimpleName(), 
            sourceIp.getHostAddress(), 
            messageId, 
            content
        );
    }

    // **************************************************************************************************************
    // Factory pattern for InternalReceivedTalkMessage

    public static IpSetter<InternalReceivedTalkMessage> create(Class<?> clazz, int messageId, String content) {
        return new Builder(clazz, messageId, content);
    }

    private static class Builder extends IpBuilder<InternalReceivedTalkMessage> {
        private Class<?> clazz;
        private String content;
        private int messageId;

        private Builder(Class<?> clazz, int messageId, String content) {
            this.clazz     = clazz;
            this.messageId = messageId;
            this.content   = content;
        }

        @Override
        protected InternalReceivedTalkMessage self() {
            return new InternalReceivedTalkMessage(this);
        }
    }

    private InternalReceivedTalkMessage(Builder builder) {
        this.clazz     = builder.clazz;
        this.messageId = builder.messageId;
        this.sourceIp  = builder.sourceIp;
        this.content   = builder.content;
    }
}
