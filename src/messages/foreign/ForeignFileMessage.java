package messages.foreign;

import interfaces.visitors.ForeignMessageVisitor;

import static utils.Constants.Strings.FILE_FORMAT;
import static utils.Constants.Strings.FILE_LOG_FORMAT;

public class ForeignFileMessage extends ForeignMessage {
    private final int MESSAGE_ID;
    private String fileName;
    private long fileSize;

    // **************************************************************************************************************
    // Inherited fields from ForeignMessage

    @Override
    protected String assembleFormattedMessage() {
        return FILE_FORMAT.formatted(
            MESSAGE_ID,
            fileName,
            fileSize
            );
    }    

    // **************************************************************************************************************
    // Visitor pattern for ForeignFileMessage

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

    @Override
    public String getPrettyMessage() {
        return FILE_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            destinationIp.getHostAddress(),
            MESSAGE_ID,
            fileName,
            fileSize
        );
    }
    
    // **************************************************************************************************************
    // Builder pattern for ForeignFileMessage

    public static LongSetter create(Class<?> clazz, int messageId, String fileName) {
        return new Builder(clazz, messageId, fileName);
    }

    public interface LongSetter {
        IpSetter<ForeignFileMessage> fileSize(long fileSize);
    }

    private static class Builder extends IpBuilder<ForeignFileMessage> implements LongSetter {
        private final int MESSAGE_ID;
        private Class<?> clazz;
        private String fileName;
        private long fileSize;

        private Builder(Class<?> clazz, int messageId, String fileName) {
            this.MESSAGE_ID = messageId;
            this.clazz      = clazz;
            this.fileName   = fileName;
        }

        @Override
        public IpSetter<ForeignFileMessage> fileSize(long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        @Override
        protected ForeignFileMessage self() {
            return new ForeignFileMessage(this);
        }
    }

    private ForeignFileMessage(Builder builder) {
        this.MESSAGE_ID       = builder.MESSAGE_ID;
        this.clazz            = builder.clazz;
        this.fileName         = builder.fileName;
        this.fileSize         = builder.fileSize;
        this.destinationIp    = builder.destinationIp;
        this.formattedMessage = assembleFormattedMessage();
    }
}
