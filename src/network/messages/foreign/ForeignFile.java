package network.messages.foreign;

import java.net.InetAddress;

import interfaces.visitors.ForeignMessageVisitor;

import static constants.Constants.Strings.FILE_LOG_FORMAT;

public class ForeignFile extends ForeignMessage {
    private int messageId;
    private String fileName;
    private long fileSize;

    public int getMessageId() {
        return messageId;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    //****************************************************************************************************************
    // Visitor pattern for ForeignMessage

    @Override
    public ForeignResponseWrapper accept(ForeignMessageVisitor visitor) {
        return visitor.visit(this);
    }

    //****************************************************************************************************************
    // Builder pattern for ForeignFile

    public interface IdSetter {
        NameSetter id(int messageId);
    }

    public interface NameSetter {
        SizeSetter fileName(String fileName);
    }

    public interface SizeSetter {
        ForeignFile fileSize(long fileSize);
    }

    protected static class Builder 
                    implements NameSetter, 
                    SizeSetter,
                                IdSetter {
        private int messageId;
        private String fileName;
        private long fileSize;
        private InetAddress sourceIp;

        @Override
        public NameSetter id(int messageId) {
            this.messageId = messageId;
            return this;
        }

        @Override
        public ForeignFile fileSize(long fileSize) {
            this.fileSize = fileSize;
            return new ForeignFile(this);
        }

        @Override
        public SizeSetter fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        protected Builder(InetAddress sourceIp) {
            this.sourceIp = sourceIp;
        }
    }

    private ForeignFile(Builder builder) {
        this.sourceIp  = builder.sourceIp;
        this.messageId = builder.messageId;
        this.fileName  = builder.fileName;
        this.fileSize  = builder.fileSize;
    }

    // ***************************************************************************************************************
    // ForeignLoggable interface for ForeignMessage
    
    @Override
    public String getPrettyMessage() {
        return FILE_LOG_FORMAT.formatted(sourceIp.getHostAddress(), messageId, fileName, fileSize);
    }

    @Override
    public String getActualMessage() {
        return decodedMessage;
    }
}
