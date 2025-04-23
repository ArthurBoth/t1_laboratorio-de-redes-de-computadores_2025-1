package messages.internal.received;

import static utils.Constants.Strings.TALK_FORMAT;
import static utils.Constants.Strings.TALK_LOG_FORMAT;

import interfaces.visitors.internal.InternalReceivedMessageVisitor;

public class InternalReceivedTalkMessage extends InternalReceivedIdMessage {
    private String messageContent;

    public String getContent() {
        return messageContent;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalReceivedTalkMessage

    @Override
    public void accept(InternalReceivedMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return TALK_FORMAT.formatted(
            messageId,
            messageContent
        );
    }

    @Override
    public String getPrettyMessage() {
        return TALK_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            sourceIp.getHostAddress(),
            messageId,
            messageContent
        );
    }

    // ****************************************************************************************************
    // Factory pattern for InternalReceivedTalkMessage

    public static IpSetter<InternalReceivedTalkMessage> create(Class<?> clazz, int messageId, String messageContent) {
        return new Builder(clazz, messageId, messageContent);
    }

    private static class Builder extends IpBuilder<InternalReceivedTalkMessage> {
        private Class<?> clazz;
        private String messageContent;
        private int messageId;

        private Builder(Class<?> clazz, int messageId, String messageContent) {
            this.clazz          = clazz;
            this.messageId      = messageId;
            this.messageContent = messageContent;
        }

        @Override
        protected InternalReceivedTalkMessage self() {
            return new InternalReceivedTalkMessage(this);
        }
    }

    private InternalReceivedTalkMessage(Builder builder) {
        this.clazz          = builder.clazz;
        this.messageId      = builder.messageId;
        this.sourceIp       = builder.sourceIp;
        this.messageContent = builder.messageContent;
    }
}
