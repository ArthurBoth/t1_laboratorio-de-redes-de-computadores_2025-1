package network.messages.foreign;

import java.net.InetAddress;

import interfaces.visitors.AcknowledgementVisitor;
import interfaces.visitors.ForeignMessageVisitor;

import static constants.Constants.Strings.NACK_LOG_FORMAT;

public class ForeignNAck extends ForeignAcknowledgement {
    private int nAckId;
    private String reason;

    public int getNonAcknowledgedId() {
        return nAckId;
    }

    public String getReason() {
        return reason;
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
    // Builder pattern for ForeignNAck

    public interface NotAckIdSetter {
        ReasonSetter nAckId(int nAckId);
    }

    public interface ReasonSetter {
        ForeignNAck reason(String reason);
    }

    public static class Builder implements NotAckIdSetter, ReasonSetter {
        private int nAckId;
        private String reason;
        private InetAddress sourceIp;

        @Override
        public ReasonSetter nAckId(int nAckId) {
            this.nAckId = nAckId;
            return this;
        }

        @Override
        public ForeignNAck reason(String reason) {
            this.reason = reason;
            return new ForeignNAck(this);
        }

        public Builder(InetAddress sourceIp) {
            this.sourceIp = sourceIp;
        }
    }

    private ForeignNAck(Builder builder) {
        this.nAckId   = builder.nAckId;
        this.reason   = builder.reason;
        this.sourceIp = builder.sourceIp;
    }

    // ***************************************************************************************************************
    // ForeignLoggable interface for ForeignMessage
    
    @Override
    public String getPrettyMessage() {
        return NACK_LOG_FORMAT.formatted(sourceIp.getHostAddress(), nAckId, reason);
    }

    @Override
    public String getActualMessage() {
        return decodedMessage;
    }
}
