package network.messages.foreign;

import java.net.InetAddress;

public class ForeignResponseWrapper {
    private InetAddress sourceIp;
    private int messageId;
    private String message;
    private boolean acknowledged;

    public InetAddress getSourceIp() {
        return sourceIp;
    }

    public int getMessageId() {
        return messageId;
    }

    public boolean ackResponse() {
        return acknowledged;
    }

    public String getMessage() {
        return message;
    }

    // **************************************************************************************************************
    // Factory pattern for ForeignResponseWrapper
    public static IpSetter ack(int messageId) {
        return new Builder(true, messageId);
    }

    public static StringSetter notAck(int messageId) {
        return new Builder(false, messageId);
    }

    // **************************************************************************************************************
    // Builder pattern for ForeignResponseWrapper

    public interface StringSetter {
        IpSetter because(String message);
    }

    public interface IpSetter {
        ForeignResponseWrapper from(InetAddress ipAddress);
    }

    private static class Builder implements StringSetter, IpSetter {
        private InetAddress sourceIp;
        private int messageId;
        private String message;
        private boolean acknowledged;

        protected Builder(boolean acknowledged, int messageId) {
            this.acknowledged = acknowledged;
            this.messageId    = messageId;
            this.sourceIp     = null;
            this.message      = null;
        }

        @Override
        public IpSetter because(String message) {
            this.message = message;
            return this;
        }

        @Override
        public ForeignResponseWrapper from(InetAddress ipAddress) {
            this.sourceIp = ipAddress;
            return new ForeignResponseWrapper(this);
        }
    }

    private ForeignResponseWrapper(Builder builder) {
        this.sourceIp     = builder.sourceIp;
        this.messageId    = builder.messageId;
        this.message      = builder.message;
        this.acknowledged = builder.acknowledged;
    }
}
