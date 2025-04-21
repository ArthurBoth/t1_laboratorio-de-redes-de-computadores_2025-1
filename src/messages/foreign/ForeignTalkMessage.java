package messages.foreign;

import interfaces.visitors.EncoderVisitor;
import interfaces.visitors.ForeignMessageVisitor;
import interfaces.visitors.LoggerVisitor;
import interfaces.visitors.MessageVisitor;

import static utils.Constants.Strings.TALK_FORMAT;
import static utils.Constants.Strings.TALK_LOG_FORMAT;

public class ForeignTalkMessage extends ForeignMessage {
    private final int MESSAGE_ID;
    private String messageContent;

    public int getMessageId() {
        return this.MESSAGE_ID;
    }

    public String getContent() {
        return this.messageContent;
    }

    // **************************************************************************************************************
    // Visitor pattern for ForeignTalkMessage

    @Override
    public void accept(ForeignMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(LoggerVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(MessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public byte[] encode(EncoderVisitor visitor) {
        return visitor.encode(this);
    }

    // **************************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return TALK_FORMAT.formatted(
            MESSAGE_ID,
            messageContent
            );
    }

    @Override
    public String getPrettyMessage() {
        return TALK_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            destinationIp.getHostAddress(),
            MESSAGE_ID,
            messageContent
        );
    }

    // **************************************************************************************************************
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
