package network.messages.foreign;

import java.net.InetAddress;

import interfaces.visitors.ForeignMessageVisitor;

import static constants.Constants.Strings.CHUNK_LOG_FORMAT;

public class ForeignChunk extends ForeignMessage {
    private int messageId;
    private int sequenceNumber;
    private byte[] chunkData;
    
    public int getMessageId() {
        return messageId;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public byte[] getChunkData() {
        return chunkData;
    }

    public long getChunkSize() {
        return chunkData.length;
    }

    //****************************************************************************************************************
    // Visitor pattern for ForeignMessage
    
    @Override
    public ForeignResponseWrapper accept(ForeignMessageVisitor visitor) {
        return visitor.visit(this);
    }

    //****************************************************************************************************************
    // Builder pattern for ForeignChunk

    public interface IdSetter {
        SeqNumberSetter id(int messageId);
    }

    public interface SeqNumberSetter {
        DataSetter sequenceNumber(int sequenceNumber);
    }

    public interface DataSetter {
        ForeignChunk chunkData(byte[] chunkData);
    }

    protected static class Builder implements DataSetter, SeqNumberSetter, IdSetter {
        private int messageId;
        private int sequenceNumber;
        private byte[] chunkData;
        private InetAddress sourceIp;

        @Override
        public SeqNumberSetter id(int messageId) {
            this.messageId = messageId;
            return this;
        }

        @Override
        public DataSetter sequenceNumber(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
            return this;
        }

        @Override
        public ForeignChunk chunkData(byte[] chunkData) {
            this.chunkData = chunkData;
            return new ForeignChunk(this);
        }

        protected Builder(InetAddress sourceIp) {
            this.sourceIp = sourceIp;
        }
    }

    private ForeignChunk(Builder builder) {
        this.sourceIp       = builder.sourceIp;
        this.messageId      = builder.messageId;
        this.sequenceNumber = builder.sequenceNumber;
        this.chunkData      = builder.chunkData;
    }

    // ***************************************************************************************************************
    // ForeignLoggable interface for ForeignMessage
    
    @Override
    public String getPrettyMessage() {
        StringBuilder stringBuilder;
        byte b;

        stringBuilder = new StringBuilder();
        for (int i = 0; i < chunkData.length; i++) {
            b = chunkData[i];
            stringBuilder.append(String.format("%02X", b));
            if (i < chunkData.length - 1) {
                stringBuilder.append(" ");
            }
        }

        return CHUNK_LOG_FORMAT.formatted(
            sourceIp.getHostAddress(),
            messageId,
            sequenceNumber,
            stringBuilder.toString(),
            chunkData.length
        );
    }

    @Override
    public String getActualMessage() {
        return decodedMessage;
    }
}
