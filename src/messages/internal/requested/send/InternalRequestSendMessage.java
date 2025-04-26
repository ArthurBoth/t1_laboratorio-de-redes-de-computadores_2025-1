package messages.internal.requested.send;

import java.net.InetAddress;
import java.net.UnknownHostException;

import messages.internal.requested.InternalRequestMessage;
import messages.internal.requested.send.InternalRequestSendNAckMessage.StringSetter;

public abstract class InternalRequestSendMessage extends InternalRequestMessage {
    protected InetAddress destinationIp;

    public InetAddress getDestinationIp() {
        return destinationIp;
    }
    // ****************************************************************************************************
    // Builder pattern for InternalRequestSendMessage

    public interface MessageSelection {
        IpSetter<InternalRequestSendTalkMessage> talk(String content);
        IpSetter<InternalRequestSendFileMessage> file(String fileName);
        IpSetter<InternalRequestSendAckMessage> ack(int messageId);
        InternalRequestSendNAckMessage.StringSetter nAck(int messageId);
    }

    protected static final class Builder implements MessageSelection {
        private Class<?> clazz;

        private Builder(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public IpSetter<InternalRequestSendTalkMessage> talk(String content) {
            return InternalRequestSendTalkMessage.create(clazz, content);
        }

        @Override
        public IpSetter<InternalRequestSendFileMessage> file(String fileName) {
            return InternalRequestSendFileMessage.create(clazz, fileName);
        }

        @Override
        public IpSetter<InternalRequestSendAckMessage> ack(int messageId) {
            return InternalRequestSendAckMessage.create(clazz, messageId);
        }

        @Override
        public StringSetter nAck(int messageId) {
            return InternalRequestSendNAckMessage.create(clazz, messageId);
        }
    }

    public static Builder build(Class<?> clazz) {
        return new Builder(clazz);
    }
    
    // ****************************************************************************************************
    // Abstract Builder pattern for InternalRequestSendMessage subclasses

    public interface IpSetter<T extends InternalRequestMessage> {
        T to(String destinationIp) throws UnknownHostException;
        T to(InetAddress destinationIp);
    }
 
    protected static abstract class IpBuilder<T extends InternalRequestMessage> implements IpSetter<T> {
        protected InetAddress destinationIp;

        @Override
        public final T to(String destinationIp) throws UnknownHostException {
            this.destinationIp = InetAddress.getByName(destinationIp);
            return self();
        }

        @Override
        public final T to(InetAddress destinationIp) {
            this.destinationIp = destinationIp;
            return self();
        }

        protected abstract T self();
    }
}
