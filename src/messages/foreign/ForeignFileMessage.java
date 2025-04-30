package messages.foreign;

import interfaces.visitors.EncoderVisitor;
import interfaces.visitors.foreign.ForeignVisitor;

public class ForeignFileMessage extends ForeignMessage {
    private final int MESSAGE_ID;
    private String fileName;
    private long fileSize;

    public int getMessageId() {
        return this.MESSAGE_ID;
    }

    public String getFileName() {
        return this.fileName;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    // ****************************************************************************************************
    // Visitor pattern for ForeignFileMessage

    @Override
    public byte[] encode(EncoderVisitor visitor) {
        return visitor.encode(this);
    }

    @Override
    public void ackcept(ForeignVisitor visitor) {
        visitor.ack(this);
    }

    @Override
    public void nackcept(ForeignVisitor visitor) {
        visitor.nack(this);
    }
    
    // ****************************************************************************************************
    // Builder pattern for ForeignFileMessage

    public static LongSetter create(int messageId, String fileName) {
        return new Builder(messageId, fileName);
    }

    public interface LongSetter {
        IpSetter<ForeignFileMessage> fileSize(long fileSize);
    }

    private static class Builder extends IpBuilder<ForeignFileMessage> implements LongSetter {
        private final int MESSAGE_ID;
        private String fileName;
        private long fileSize;

        private Builder(int messageId, String fileName) {
            this.MESSAGE_ID = messageId;
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
        this.MESSAGE_ID    = builder.MESSAGE_ID;
        this.fileName      = builder.fileName;
        this.fileSize      = builder.fileSize;
        this.destinationIp = builder.destinationIp;
        this.port          = builder.port;
    }
}
