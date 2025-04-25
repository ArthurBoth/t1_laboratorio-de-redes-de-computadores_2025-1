package messages.foreign;

import interfaces.visitors.EncoderVisitor;

public class ForeignEndMessage extends ForeignMessage {
    private final int MESSAGE_ID;
    private String fileHash;

    public int getMessageId() {
        return this.MESSAGE_ID;
    }

    public String getFileHash() {
        return this.fileHash;
    }

    // ****************************************************************************************************
    // Visitor pattern for ForeignEndMessage

    @Override
    public byte[] encode(EncoderVisitor visitor) {
        return visitor.encode(this);
    }

    // ****************************************************************************************************
    // Builder pattern for ForeignEndMessage

    public static IpBuilder<ForeignEndMessage> create(int messageId, String fileHash) {
        return new Builder(messageId, fileHash);
    }

    private static class Builder extends IpBuilder<ForeignEndMessage> {
        private final int MESSAGE_ID;
        private String fileHash;

        private Builder(int messageId, String fileHash) {
            this.MESSAGE_ID = messageId;
            this.fileHash   = fileHash;
        }

        @Override
        protected ForeignEndMessage self() {
            return new ForeignEndMessage(this);
        }
    }

    private ForeignEndMessage(Builder builder) {
        this.MESSAGE_ID = builder.MESSAGE_ID;
        this.fileHash   = builder.fileHash;
    }
}
