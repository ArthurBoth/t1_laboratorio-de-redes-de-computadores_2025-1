package messages.internal.requested.send;

import static utils.Constants.Strings.SIMPLE_TALK_FORMAT;
import static utils.Constants.Strings.TALK_SENDING_LOG_FORMAT;

import interfaces.visitors.internal.InternalRequestMessageVisitor;

public class InternalRequestSendTalkMessage extends InternalRequestSendMessage {
    private String content;

    public String getContent() {
        return content;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalSentTalkMessage

    @Override
    public void accept(InternalRequestMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return SIMPLE_TALK_FORMAT.formatted(
            clazz.getSimpleName(),
            content
            );
    }

    @Override
    public String getPrettyMessage() {
        return TALK_SENDING_LOG_FORMAT.formatted(
            clazz.getSimpleName(), 
            content
            );
    }

    // ****************************************************************************************************
    // Builder pattern for InternalSentTalkMessage

    public static IpSetter<InternalRequestSendTalkMessage> create(Class<?> clazz, String content) {
        return new Builder(clazz, content);
    }

    private static class Builder extends IpBuilder<InternalRequestSendTalkMessage> {
        private Class<?> clazz;
        private String content;

        private Builder(Class<?> clazz, String content) {
            this.clazz   = clazz;
            this.content = content;
        }

        @Override
        public InternalRequestSendTalkMessage self() {
            return new InternalRequestSendTalkMessage(this);
        }
    }

    private InternalRequestSendTalkMessage(Builder builder) {
        this.clazz         = builder.clazz;
        this.destinationIp = builder.destinationIp;
        this.content       = builder.content;
    }
}
