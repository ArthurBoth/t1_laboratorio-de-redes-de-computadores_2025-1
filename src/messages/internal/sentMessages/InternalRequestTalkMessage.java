package messages.internal.sentMessages;

import interfaces.visitors.InternalMessageVisitor;
import interfaces.visitors.InternalSentMessageVisitor;

import static utils.Constants.Strings.TALK_SENDING_REQUEST_FORMAT;

public class InternalRequestTalkMessage extends InternalRequestMessage {
    private String content;

    public String getContent() {
        return content;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalSentTalkMessage

    @Override
    public void accept(InternalMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(InternalSentMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return TALK_SENDING_REQUEST_FORMAT.formatted(
            clazz.getSimpleName(), 
            content
            );
    }

    // ****************************************************************************************************
    // Builder pattern for InternalSentTalkMessage

    public static IpSetter<InternalRequestTalkMessage> create(Class<?> clazz, String content) {
        return new Builder(clazz, content);
    }

    private static class Builder extends IpBuilder<InternalRequestTalkMessage> {
        private Class<?> clazz;
        private String content;

        private Builder(Class<?> clazz, String content) {
            this.clazz   = clazz;
            this.content = content;
        }

        @Override
        public InternalRequestTalkMessage self() {
            return new InternalRequestTalkMessage(this);
        }
    }

    private InternalRequestTalkMessage(Builder builder) {
        this.clazz         = builder.clazz;
        this.destinationIp = builder.destinationIp;
        this.content       = builder.content;
    }
}
