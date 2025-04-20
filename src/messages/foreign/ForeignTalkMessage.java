package messages.foreign;

import interfaces.visitors.ForeignMessageVisitor;

import static utils.Constants.Strings.TALK_FORMAT;

public class ForeignTalkMessage extends ForeignMessage {
    private final int MESSAGE_ID;
    private String messageContent;

    // **************************************************************************************************************
    // Inherited fields from ForeignMessage

    @Override
    protected String assembleFormattedMessage() {
        return TALK_FORMAT.formatted(
            MESSAGE_ID,
            messageContent
            );
    }    

    // **************************************************************************************************************
    // Visitor pattern for ForeignTalkMessage

    @Override
    public void accept(ForeignMessageVisitor visitor) {
        visitor.visit(this);
    }

    // **************************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return "(%s) %s".formatted(
            clazz.getSimpleName(),
            formattedMessage
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
        this.formattedMessage = assembleFormattedMessage();
    }
}
