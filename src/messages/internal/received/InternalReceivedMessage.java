package messages.internal.received;

import java.net.InetAddress;

import interfaces.visitors.internal.InternalMessageVisitor;
import interfaces.visitors.internal.InternalReceivedMessageVisitor;
import messages.internal.InternalMessage;

public abstract class InternalReceivedMessage extends InternalMessage {
    // ****************************************************************************************************
    // The InternalReceivedMessage class is the base class for all messages received from the network.
    //    It should be used to map the messages received from the network to the internal messages of the
    //    system.
    // ****************************************************************************************************
    protected InetAddress sourceIp;
    protected int port;

    public InetAddress getSourceIp() {
        return sourceIp;
    }

    public int getPort() {
        return port;
    }

    // ****************************************************************************************************
    // Factory method for InternalReceivedIdMessage subclasses

    public static InternalReceivedIdMessage.MessageSelection create(Class<?> clazz, int messageId) {
        return InternalReceivedIdMessage.createWithId(clazz, messageId);
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalReceivedMessage

    public abstract void accept(InternalMessageVisitor visitor);

    public abstract void accept(InternalReceivedMessageVisitor visitor);

    // ****************************************************************************************************
    // Builder pattern for InternalReceivedMessage

    public interface MessageSelection {
        InternalReceivedHeartbeatMessage.StringSetter heartbeat();
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
        public InternalReceivedHeartbeatMessage.StringSetter heartbeat() {
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
    
    // ****************************************************************************************************
    // Abstract Builder pattern for InternalReceivedMessage subclasses


    public interface IpSetter<T extends InternalReceivedMessage> {
        PortSetter<T> from(InetAddress sourceIp);
    }

    public interface PortSetter<T extends InternalReceivedMessage> {
        T at(int port);
    }

    protected static abstract class IpBuilder<T extends InternalReceivedMessage>
                                implements IpSetter<T>, PortSetter<T> {
        protected InetAddress sourceIp;
        protected int port;

        @Override
        public final PortSetter<T> from(InetAddress sourceIp) {
            this.sourceIp = sourceIp;
            return this;
        }

        @Override
        public final T at(int port) {
            this.port = port;
            return self();
        }

        protected abstract T self();
    } 
}
