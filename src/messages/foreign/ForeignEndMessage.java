package messages.foreign;

import interfaces.visitors.EncoderVisitor;
import interfaces.visitors.ForeignMessageVisitor;

import static utils.Constants.Strings.END_FORMAT;
import static utils.Constants.Strings.END_LOG_FORMAT;

public class ForeignEndMessage extends ForeignMessage {
    private final int MESSAGE_ID;
    private String fileHash;

    public int getMessageId() {
        return this.MESSAGE_ID;
    }

    public String getFileHash() {
        return this.fileHash;
    }
    // **************************************************************************************************************
    // Visitor pattern for ForeignEndMessage

    @Override
    public void accept(ForeignMessageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public byte[] encode(EncoderVisitor visitor) {
        return visitor.encode(this);
    }

    // **************************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return END_FORMAT.formatted(
            MESSAGE_ID,
            fileHash
            );
    }

    @Override
    public String getPrettyMessage() {
        return END_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            destinationIp.getHostAddress(),
            MESSAGE_ID,
            fileHash
            );
    }

    // **************************************************************************************************************
    // Builder pattern for ForeignEndMessage

    public static IpSetter<ForeignEndMessage> create(Class<?> clazz, int messageId, String fileHash) {
        return new Builder(clazz, messageId, fileHash);
    }

    private static class Builder extends IpBuilder<ForeignEndMessage> {
        private final int MESSAGE_ID;
        private Class<?> clazz;
        private String fileHash;

        private Builder(Class<?> clazz, int messageId, String fileHash) {
            this.MESSAGE_ID = messageId;
            this.clazz      = clazz;
            this.fileHash   = fileHash;
        }

        @Override
        protected ForeignEndMessage self() {
            return new ForeignEndMessage(this);
        }
    }

    private ForeignEndMessage(Builder builder) {
        this.MESSAGE_ID = builder.MESSAGE_ID;
        this.clazz      = builder.clazz;
        this.fileHash   = builder.fileHash;
    }
}
