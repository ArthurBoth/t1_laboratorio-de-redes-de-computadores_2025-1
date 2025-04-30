package messages.internal.requested.send;

import static utils.Constants.Strings.END_SENDING_LOG_FORMAT;
import static utils.Constants.Strings.SIMPLE_END_FORMAT;

import interfaces.visitors.LoggerVisitor;
import interfaces.visitors.internal.InternalRequestMessageVisitor;

public class InternalRequestSendEndMessage extends InternalRequestSendMessage {
    private String fileHash;

    public String getHash() {
        return fileHash;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalRequestSendEndMessage

    @Override
    public void accept(InternalRequestMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(LoggerVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return SIMPLE_END_FORMAT.formatted(
            fileHash
        );
    }

    @Override
    public String getPrettyMessage() {
        return END_SENDING_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            fileHash
        );
    }

    // ****************************************************************************************************
    // Builder pattern for InternalRequestSendEndMessage

    protected static IpSetter<InternalRequestSendEndMessage> create(Class<?> clazz, String fileHash) {
        return new Builder(clazz, fileHash);
    }

    private static class Builder extends IpBuilder<InternalRequestSendEndMessage> {
        private Class<?> clazz;
        private String fileHash;

        private Builder(Class<?> clazz, String fileHash) {
            this.clazz = clazz;
            this.fileHash = fileHash;
        }

        @Override
        protected InternalRequestSendEndMessage self() {
            return new InternalRequestSendEndMessage(this);
        }
    }

    private InternalRequestSendEndMessage(Builder builder) {
        this.clazz         = builder.clazz;
        this.fileHash      = builder.fileHash;
        this.destinationIp = builder.destinationIp;
        this.port          = builder.port;
    }
}
