package messages.foreign;

import interfaces.visitors.ForeignMessageVisitor;

import static utils.Constants.Strings.HEARTBEAT_LOG_FORMAT;
import static utils.Constants.Strings.HEARTBEAT_MESSAGE;

public class ForeignHeartbeatMessage extends ForeignMessage {

    // **************************************************************************************************************
    // Inherited fields from ForeignMessage

    @Override
    protected String assembleFormattedMessage() {
        return HEARTBEAT_MESSAGE;
    }

    // **************************************************************************************************************
    // Visitor pattern for ForeignHeartbeatMessage

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
        return HEARTBEAT_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            destinationIp.getHostAddress()
        );
    }

    // **************************************************************************************************************
    // Builder pattern for ForeignHeartbeatMessage

    public static IpSetter<ForeignHeartbeatMessage> create(Class<?> clazz) {
        return new Builder(clazz);
    }

    private static class Builder extends IpBuilder<ForeignHeartbeatMessage> {
        private Class<?> clazz;

        private Builder (Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        protected ForeignHeartbeatMessage self() {
            return new ForeignHeartbeatMessage(this);
        }
    }

    private ForeignHeartbeatMessage(Builder builder) {
        this.clazz            = builder.clazz;
        this.destinationIp    = builder.destinationIp;
        this.formattedMessage = assembleFormattedMessage();
    }
}
