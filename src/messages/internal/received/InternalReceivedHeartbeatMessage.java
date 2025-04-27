package messages.internal.received;

import static utils.Constants.Strings.HEARTBEAT_LOG_FORMAT;

import interfaces.visitors.internal.InternalReceivedMessageVisitor;

public class InternalReceivedHeartbeatMessage extends InternalReceivedMessage {
    private String name;

    public String getName() {
        return name;
    }

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

    public static StringSetter createHeartbeat(Class<?> clazz) {
        return new Builder(clazz);
    }

    public interface StringSetter {
        IpSetter<InternalReceivedHeartbeatMessage> name(String name);
    }

    private static class Builder extends IpBuilder<InternalReceivedHeartbeatMessage> implements StringSetter {
        private Class<?> clazz;
        private String name;

        private Builder(Class<?> clazz) {
            this.clazz     = clazz;
        }

        @Override
        protected InternalReceivedHeartbeatMessage self() {
            return new InternalReceivedHeartbeatMessage(this);
        }

        @Override
        public IpSetter<InternalReceivedHeartbeatMessage> name(String name) {
            this.name = name;
            return this;
        }
    }

    private InternalReceivedHeartbeatMessage(Builder builder) {
        this.clazz    = builder.clazz;
        this.name     = builder.name;
        this.port     = builder.port;
        this.sourceIp = builder.sourceIp;
    }
}
