package network.messages.foreign;

import java.net.InetAddress;

import interfaces.visitors.AcknowledgementVisitor;
import interfaces.visitors.ForeignMessageVisitor;

import static constants.Constants.Strings.ACK_LOG_FORMAT;

public class ForeignAck extends ForeignAcknowledgement {
    private int ackId;
    
    public int getAcknowledgementId() {
        return ackId;
    }

    // *****************************************************************************************************************
    // Visitor patterns for ForeignMessage and ForeignAcknowledgement

    @Override
    public ForeignResponseWrapper accept(ForeignMessageVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void accept(AcknowledgementVisitor visitor) {
        visitor.visit(this);
    }

    // *****************************************************************************************************************
    // Builder pattern for ForeignAck

    public interface AckIdSetter {
        DecodedSetter ackId(int ackId);
    }

    public interface DecodedSetter {
        ForeignAck decodedMessage(String decodedMessage);
    }

    protected static class Builder implements AckIdSetter, DecodedSetter {
        private InetAddress sourceIp;
        private int ackId;
        private String decodedMessage;

        @Override
        public DecodedSetter ackId(int ackId) {
            this.ackId = ackId;
            return this;
        }

        @Override
        public ForeignAck decodedMessage(String decodedMessage) {
            this.decodedMessage = decodedMessage;
            return new ForeignAck(this);
        }

        public Builder(InetAddress sourceIp) {
            this.sourceIp = sourceIp;
        }
    }

    private ForeignAck(Builder builder) {
        this.sourceIp       = builder.sourceIp;
        this.ackId          = builder.ackId;
        this.decodedMessage = builder.decodedMessage;
    }

    // ***************************************************************************************************************
    // ForeignLoggable interface for ForeignMessage
    
    @Override
    public String getPrettyMessage() {
        return ACK_LOG_FORMAT.formatted(sourceIp.getHostAddress(), ackId);
    }

    @Override
    public String getActualMessage() {
        return decodedMessage;
    }
}
