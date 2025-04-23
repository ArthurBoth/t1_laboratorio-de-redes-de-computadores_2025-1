package messages.internal.receivedMessages;

import interfaces.visitors.FileMessageVisitor;
import interfaces.visitors.InternalMessageVisitor;

import static utils.Constants.Strings.FILE_FORMAT;
import static utils.Constants.Strings.FILE_LOG_FORMAT;

public class InternalReceivedFileMessage extends InternalReceivedFileRelated {
     private String fileName;
     private long fileSize;

     public String getFileName() {
         return fileName;
     }

    public long getFileSize() {
        return fileSize;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalReceivedFileMessage

    @Override
    public void accept(InternalMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(FileMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return FILE_FORMAT.formatted(
            messageId,
            fileName,
            fileSize
        );
    }

    @Override
    public String getPrettyMessage() {
        return FILE_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            sourceIp.getHostAddress(), 
            messageId, 
            fileName, 
            fileSize
        );
    }

    // ****************************************************************************************************
    // Builder pattern for InternalReceivedFileMessage

    public static LongSetter create(Class<?> clazz, int messageId, String fileName) {
        return new Builder(clazz, messageId, fileName);
    }

    public interface LongSetter {
        IpSetter<InternalReceivedFileMessage> size(long fileSize);
    }

    private static class Builder extends IpBuilder<InternalReceivedFileMessage> implements LongSetter {
        private Class<?> clazz;
        private String fileName;
        private long fileSize;
        private int messageId;

        private Builder(Class<?> clazz, int messageId, String fileName) {
            this.clazz     = clazz;
            this.messageId = messageId;
            this.fileName  = fileName;
        }

        @Override
        public IpSetter<InternalReceivedFileMessage> size(long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        @Override
        protected InternalReceivedFileMessage self() {
            return new InternalReceivedFileMessage(this);
        }
    }

    private InternalReceivedFileMessage(Builder builder) {
        this.clazz     = builder.clazz;
        this.messageId = builder.messageId;
        this.sourceIp  = builder.sourceIp;
        this.fileName  = builder.fileName;
        this.fileSize  = builder.fileSize;
    }
}
