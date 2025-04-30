package messages.internal.requested.send;

import static utils.Constants.Strings.FILE_SENDING_LOG_FORMAT;
import static utils.Constants.Strings.SIMPLE_FILE_FORMAT;

import interfaces.visitors.FileMessageVisitor;
import interfaces.visitors.LoggerVisitor;
import interfaces.visitors.internal.InternalRequestMessageVisitor;

public class InternalRequestSendFileMessage extends InternalRequestSendMessage {
    private String fileName;
    private long fileSize;

    public long getFileSize() {
        return fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalSentFileMessage

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
        return SIMPLE_FILE_FORMAT.formatted(
            fileName,
            fileSize
        );
    }

    @Override
    public String getPrettyMessage() {
        return FILE_SENDING_LOG_FORMAT.formatted(
            clazz.getSimpleName(), 
            fileName
            );
    }

    // ****************************************************************************************************
    // Builder pattern for InternalSentFileMessage

    public static IpBuilder<InternalRequestSendFileMessage> create(Class<?> clazz, String fileName) {
        return new MandatoryBuilder(clazz, fileName);
    }

    private static class MandatoryBuilder extends IpBuilder<InternalRequestSendFileMessage> {
        private Class<?> clazz;
        private String fileName;
        private long fileSize;

        private MandatoryBuilder(Class<?> clazz, String fileName) {
            this.clazz    = clazz;
            this.fileName = fileName;
        }

        @Override
        protected InternalRequestSendFileMessage self() {
            return new InternalRequestSendFileMessage(this);
        }
    }

    private InternalRequestSendFileMessage(MandatoryBuilder builder) {
        this.clazz         = builder.clazz;
        this.destinationIp = builder.destinationIp;
        this.fileName      = builder.fileName;
        this.fileSize      = builder.fileSize;
        this.port          = builder.port;
    }
}
