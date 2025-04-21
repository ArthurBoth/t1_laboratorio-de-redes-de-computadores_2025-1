package messages.internal.receivedMessages;

import java.net.InetAddress;

import messages.internal.InternalMessage;

public abstract class InternalReceivedMessage extends InternalMessage {
    // **************************************************************************************************************
    // The InternalReceivedMessage class is the base class for all messages received from the network.
    //    It should be used to map the messages received from the network to the internal messages of the system.
    // **************************************************************************************************************
    protected InetAddress sourceIp;

    public InetAddress getSourceIp() {
        return sourceIp;
    }

    // **************************************************************************************************************
    // Factory method for InternalReceivedIdMessage subclasses

    public static InternalReceivedIdMessage.MessageSelection create(Class<?> clazz, int messageId) {
        return InternalReceivedIdMessage.createWithId(clazz, messageId);
    }

    // **************************************************************************************************************
    // Builder pattern for InternalReceivedMessage

    public interface MessageSelection {
        IpSetter<InternalReceivedHeartbeatMessage> heartbeat();
        IpSetter<InternalReceivedAckMessage> ack(int messageId);
        InternalReceivedNAckMessage.StringSetter nAck(int messageId);
        IpSetter<InternalReceivedUnsupportedMessage> unsupportedMessage(String content);
    }

    private static final class Builder implements MessageSelection {
        private Class<?> clazz;

        private Builder(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public IpSetter<InternalReceivedHeartbeatMessage> heartbeat() {
            return InternalReceivedHeartbeatMessage.createHeartbeat(clazz);
        }

        @Override
        public IpSetter<InternalReceivedAckMessage> ack(int messageId) {
            return InternalReceivedAckMessage.ack(clazz, messageId);
        }

        @Override
        public InternalReceivedNAckMessage.StringSetter nAck(int messageId) {
            return InternalReceivedNAckMessage.nAck(clazz, messageId);
        }

        @Override
        public IpSetter<InternalReceivedUnsupportedMessage> unsupportedMessage(String content) {
            return InternalReceivedUnsupportedMessage.create(clazz, content);
        }
    }

    public static InternalReceivedMessage.MessageSelection create(Class<?> clazz) {
        return new Builder(clazz);
    }
    
    // **************************************************************************************************************
    // Abstract Builder pattern for InternalReceivedMessage subclasses


    public interface IpSetter<T extends InternalReceivedMessage> {
        T from(InetAddress sourceIp);
    }

    protected static abstract class IpBuilder<T extends InternalReceivedMessage> implements IpSetter<T> {
        protected InetAddress sourceIp;

        @Override
        public final T from(InetAddress sourceIp) {
            this.sourceIp = sourceIp;
            return self();
        }

        protected abstract T self();
    } 
}
