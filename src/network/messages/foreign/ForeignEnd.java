package network.messages.foreign;

import java.net.InetAddress;

import interfaces.visitors.ForeignMessageVisitor;

import static constants.Constants.Strings.END_LOG_FORMAT;

public class ForeignEnd extends ForeignMessage {
    private int messaegId;
    private String hash;

    public int getMessageId() {
        return messaegId;
    }
    
    public String getHash() {
        return hash;
    }

    //****************************************************************************************************************
    // Visitor pattern for ForeignMessage

    @Override
    public ForeignResponseWrapper accept(ForeignMessageVisitor visitor) {
        return visitor.visit(this);
    }

    //****************************************************************************************************************
    // Builder pattern for ForeignEnd

    public interface IdSetter {
        HashSetter id(int messageId);        
    }

    public interface HashSetter {
        ForeignEnd hash(String hash);
    }

    protected static class Builder implements IdSetter, HashSetter {
        private int messageId;
        private String hash;
        private InetAddress sourceIp;

        @Override
        public ForeignEnd hash(String hash) {
            this.hash = hash;
            return new ForeignEnd(this);
        }

        @Override
        public HashSetter id(int messageId) {
            this.messageId = messageId;
            return this;
        }

        protected Builder(InetAddress sourceIp) {
            this.sourceIp = sourceIp;
        }
    }

    private ForeignEnd(Builder builder) {
        this.sourceIp  = builder.sourceIp;
        this.messaegId = builder.messageId;
        this.hash      = builder.hash;
    }

    // ***************************************************************************************************************
    // ForeignLoggable interface for ForeignMessage
    
    @Override
    public String getPrettyMessage() {
        return END_LOG_FORMAT.formatted(sourceIp.getHostAddress(), messaegId, hash);
    }

    @Override
    public String getActualMessage() {
        return decodedMessage;
    }
}
