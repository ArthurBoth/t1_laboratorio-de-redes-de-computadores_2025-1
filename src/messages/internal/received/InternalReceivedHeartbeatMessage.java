package messages.internal.received;

import static utils.Constants.Strings.HEARTBEAT_LOG_FORMAT;

import interfaces.visitors.internal.InternalReceivedMessageVisitor;

public class InternalReceivedHeartbeatMessage extends InternalReceivedMessage {

    // ****************************************************************************************************
    // Visitor pattern for InternalReceivedHeartbeatMessage

    @Override
    public void accept(InternalReceivedMessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return HEARTBEAT_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            sourceIp.getHostAddress()
        );
    }

    @Override
    public String getPrettyMessage() {
        return HEARTBEAT_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            sourceIp.getHostAddress()
        );
    }

    // ****************************************************************************************************
    // Factory pattern for InternalReceivedHeartbeatMessage

    public static IpSetter<InternalReceivedHeartbeatMessage> createHeartbeat(Class<?> clazz) {
        return new Builder(clazz);
    }

    private static class Builder extends IpBuilder<InternalReceivedHeartbeatMessage> {
        private Class<?> clazz;

        private Builder(Class<?> clazz) {
            this.clazz     = clazz;
        }

        @Override
        protected InternalReceivedHeartbeatMessage self() {
            return new InternalReceivedHeartbeatMessage(this);
        }
    }

    private InternalReceivedHeartbeatMessage(Builder builder) {
        this.clazz    = builder.clazz;
        this.sourceIp = builder.sourceIp;
    }
}
