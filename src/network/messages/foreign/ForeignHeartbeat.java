package network.messages.foreign;

import java.net.InetAddress;

import interfaces.visitors.ForeignMessageVisitor;

import static constants.Constants.Strings.HEARTBEAT_LOG_FORMAT;

public class ForeignHeartbeat extends ForeignMessage {

    // *****************************************************************************************************************
    // Visitor pattern for ForeignMessage
    
    @Override
    public ForeignResponseWrapper accept(ForeignMessageVisitor visitor) {
        return visitor.visit(this);
    }

    // *****************************************************************************************************************
    // Builder pattern for ForeignHeartbeat

    public interface DecodedMessageSetter {
        ForeignHeartbeat decodedMessage(String decodedMessage);
    }

    protected static class Builder implements DecodedMessageSetter {
        private String decodedMessage;
        private InetAddress sourceIp;

        @Override
        public ForeignHeartbeat decodedMessage(String decodedMessage) {
            this.decodedMessage = decodedMessage;
            return new ForeignHeartbeat(this);
        }

        protected Builder(InetAddress sourceIp) {
            this.sourceIp = sourceIp;
        }
    }

    private ForeignHeartbeat(Builder builder) {
        this.sourceIp       = builder.sourceIp;
        this.decodedMessage = builder.decodedMessage;
    }

    // ***************************************************************************************************************
    // ForeignLoggable interface for ForeignMessage
    
    @Override
    public String getPrettyMessage() {
        return HEARTBEAT_LOG_FORMAT.formatted(sourceIp.getHostAddress());
    }

    @Override
    public String getActualMessage() {
        return decodedMessage;
    }
}
