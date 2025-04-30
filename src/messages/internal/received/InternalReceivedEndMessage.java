package messages.internal.received;

import static utils.Constants.Strings.END_FORMAT;
import static utils.Constants.Strings.END_LOG_FORMAT;

import interfaces.visitors.FileMessageVisitor;
import interfaces.visitors.LoggerVisitor;
import interfaces.visitors.internal.InternalMessageVisitor;
import interfaces.visitors.internal.InternalReceivedMessageVisitor;

public class InternalReceivedEndMessage extends InternalReceivedFileRelated {
    private String fileHash;

    public String getFileHash() {
        return fileHash;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalReceivedEndMessage

    @Override
    public void accept(InternalReceivedMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(FileMessageVisitor visitor) {
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
        return END_FORMAT.formatted(
            messageId,
            fileHash
        );
    }

    @Override
    public String getPrettyMessage() {
        return END_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            sourceIp.getHostAddress(),
            messageId,
            fileHash
        );
    }

    // ****************************************************************************************************
    // Factory pattern for InternalReceivedEndMessage

    public static IpSetter<InternalReceivedEndMessage> create(Class<?> clazz, int messageId, String fileHash) {
        return new Builder(clazz, messageId, fileHash);
    }

    private static class Builder extends IpBuilder<InternalReceivedEndMessage> {
        private Class<?> clazz;
        private String fileHash;
        private int messageId;

        private Builder(Class<?> clazz, int messageId, String fileHash) {
            this.clazz     = clazz;
            this.messageId = messageId;
            this.fileHash  = fileHash;
        }

        @Override
        protected InternalReceivedEndMessage self() {
            return new InternalReceivedEndMessage(this);
        }
    }
    private InternalReceivedEndMessage(Builder builder) {
        this.clazz     = builder.clazz;
        this.messageId = builder.messageId;
        this.sourceIp  = builder.sourceIp;
        this.fileHash  = builder.fileHash;
        this.port      = builder.port;
    }
}
