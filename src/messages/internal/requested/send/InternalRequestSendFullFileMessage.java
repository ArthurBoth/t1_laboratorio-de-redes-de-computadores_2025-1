package messages.internal.requested.send;

import static utils.Constants.Strings.FULL_FILE_REQUEST;
import static utils.Constants.Strings.FULL_FILE_REQ_LOG_FORMAT;

import interfaces.visitors.FileMessageVisitor;
import interfaces.visitors.LoggerVisitor;
import interfaces.visitors.internal.InternalRequestMessageVisitor;

public class InternalRequestSendFullFileMessage extends InternalRequestSendMessage {
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalRequestSendFullFileMessage

    @Override
    public void accept(InternalRequestMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(LoggerVisitor visitor) {
        visitor.visit(this);
    }

    public void accept(FileMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return FULL_FILE_REQUEST;
    }

    @Override
    public String getPrettyMessage() {
        return FULL_FILE_REQ_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            fileName
        );
    }

    // ****************************************************************************************************
    // Builder pattern for InternalRequestSendFullFileMessage

    public static IpSetter<InternalRequestSendFullFileMessage> create(Class<?> clazz, String fileName) {
        return new Builder(clazz, fileName);
    }

    private static class Builder extends IpBuilder<InternalRequestSendFullFileMessage> {
        private Class<?> clazz;
        private String fileName;

        private Builder(Class<?> clazz, String fileName) {
            this.clazz    = clazz;
            this.fileName = fileName;
        }

        @Override
        protected InternalRequestSendFullFileMessage self() {
            return new InternalRequestSendFullFileMessage(this);
        }
    }

    private InternalRequestSendFullFileMessage(Builder builder) {
        this.clazz         = builder.clazz;
        this.fileName      = builder.fileName;
        this.destinationIp = builder.destinationIp;
        this.port          = builder.port;
    }
}
