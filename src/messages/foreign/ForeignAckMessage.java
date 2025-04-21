package messages.foreign;

import interfaces.visitors.ForeignMessageVisitor;

import static utils.Constants.Strings.ACK_FORMAT;
import static utils.Constants.Strings.ACK_LOG_FORMAT;

public class ForeignAckMessage extends ForeignMessage {
    private final int ACKKED_MESSAGE_ID;

    // **************************************************************************************************************
    // Inherited fields from ForeignMessage

    @Override
    protected String assembleFormattedMessage() {
        return ACK_FORMAT.formatted(ACKKED_MESSAGE_ID);
    }

    // **************************************************************************************************************
    // Visitor pattern for ForeignAckMessage

    @Override
    public void accept(ForeignMessageVisitor visitor) {
        visitor.visit(this);
    }

    // **************************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return "(%s) %s".formatted(
            clazz.getSimpleName(),
            formattedMessage
            );
    }

    @Override
    public String getPrettyMessage() {
        return ACK_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            destinationIp.getHostAddress(),
            ACKKED_MESSAGE_ID
            );
    }
    
    // **************************************************************************************************************
    // Builder pattern for ForeignAckMessage

    public static IpSetter<ForeignAckMessage> create(Class<?> clazz, int ackkedMessageId) {
        return new Builder(clazz, ackkedMessageId);
    }

    private static class Builder extends IpBuilder<ForeignAckMessage> {
        private final int ACKKED_MESSAGE_ID;
        private Class<?> clazz;

        private Builder(Class<?> clazz, int ackkedMessageId) {
            this.ACKKED_MESSAGE_ID = ackkedMessageId;
            this.clazz             = clazz;
        }

        @Override
        protected ForeignAckMessage self() {
            return new ForeignAckMessage(this);
        }
    }

    private ForeignAckMessage(Builder builder) {
        this.ACKKED_MESSAGE_ID = builder.ACKKED_MESSAGE_ID;
        this.clazz             = builder.clazz;
        this.destinationIp     = builder.destinationIp;
    }
}
