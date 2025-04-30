package messages.internal.received;

import static utils.Constants.Strings.UNSUPPORTED_FORMAT;
import static utils.Constants.Strings.UNSUPPORTED_LOG_FORMAT;

import interfaces.visitors.LoggerVisitor;
import interfaces.visitors.internal.InternalMessageVisitor;
import interfaces.visitors.internal.InternalReceivedMessageVisitor;


public class InternalReceivedUnsupportedMessage extends InternalReceivedMessage {
    private String content;

    // ****************************************************************************************************
    // Visitor pattern for InternalReceivedUnsupportedMessage

    @Override
    public void accept(InternalReceivedMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(LoggerVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(InternalMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return UNSUPPORTED_FORMAT.formatted(
            clazz.getSimpleName(),
            sourceIp.getHostAddress(),
            content
        );
    }

    @Override
    public String getPrettyMessage() {
        return UNSUPPORTED_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            sourceIp.getHostAddress(), 
            content
        );
    }

    // ****************************************************************************************************
    // Factory pattern for InternalReceivedUnsupportedMessage

    public static IpSetter<InternalReceivedUnsupportedMessage> create(Class<?> clazz, String content) {
        return new Builder(clazz, content);
    }

    private static class Builder extends IpBuilder<InternalReceivedUnsupportedMessage> {
        private Class<?> clazz;
        private String content;

        private Builder(Class<?> clazz, String content) {
            this.clazz     = clazz;
            this.content   = content;
        }

        @Override
        protected InternalReceivedUnsupportedMessage self() {
            return new InternalReceivedUnsupportedMessage(this);
        }
    }

    private InternalReceivedUnsupportedMessage(Builder builder) {
        this.clazz     = builder.clazz;
        this.sourceIp  = builder.sourceIp;
        this.content   = builder.content;
        this.port      = builder.port;
    }
}
