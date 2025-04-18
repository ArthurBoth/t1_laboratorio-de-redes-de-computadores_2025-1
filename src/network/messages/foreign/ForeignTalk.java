package network.messages.foreign;

import java.net.InetAddress;

import interfaces.visitors.ForeignMessageVisitor;

import static constants.Constants.Strings.TALK_LOG_FORMAT;

public class ForeignTalk extends ForeignMessage {
    private int messageId;
    private String message;

    public int getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    //****************************************************************************************************************
    // Visitor pattern for ForeignMessage

    @Override
    public ForeignResponseWrapper accept(ForeignMessageVisitor visitor) {
        return visitor.visit(this);
    }

    //****************************************************************************************************************
    // Builder pattern for ForeignTalk

    public interface IdSetter {
        MessageSetter id(int messageId);
    }

    public interface MessageSetter {
        DecodedSetter message(String message);
    }

    public interface DecodedSetter {
        ForeignTalk decodedMessage(String decodedMessage);
    }

    protected static class Builder implements MessageSetter, IdSetter, DecodedSetter {
        private int messageId;
        private String message;
        private InetAddress sourceIp;
        private String decodedMessage;

        @Override
        public MessageSetter id(int messageId) {
            this.messageId = messageId;
            return this;
        }

        @Override
        public DecodedSetter message(String message) {
            this.message = message;
            return this;
        }

        @Override
        public ForeignTalk decodedMessage(String decodedMessage) {
            this.decodedMessage = decodedMessage;
            return new ForeignTalk(this);
        }

        public Builder(InetAddress sourceIp) {
            this.sourceIp = sourceIp;
        }
    }

    private ForeignTalk(Builder builder) {
        this.sourceIp       = builder.sourceIp;
        this.messageId      = builder.messageId;
        this.message        = builder.message;
        this.decodedMessage = builder.decodedMessage;
    }

    // ***************************************************************************************************************
    // ForeignLoggable interface for ForeignMessage
    
    @Override
    public String getPrettyMessage() {
        return TALK_LOG_FORMAT.formatted(sourceIp.getHostAddress(), messageId, message);
    }

    @Override
    public String getActualMessage() {
        return decodedMessage;
    }
}
