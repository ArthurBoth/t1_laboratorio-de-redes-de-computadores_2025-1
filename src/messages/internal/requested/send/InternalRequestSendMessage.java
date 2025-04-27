package messages.internal.requested.send;

import java.net.InetAddress;
import java.net.UnknownHostException;

import messages.internal.requested.InternalRequestMessage;

public abstract class InternalRequestSendMessage extends InternalRequestMessage {
    protected InetAddress destinationIp;
    protected int port;

    public InetAddress getDestinationIp() {
        return destinationIp;
    }

    public int getPort() {
        return port;
    }

    // ****************************************************************************************************
    // Builder pattern for InternalRequestSendMessage

    public interface MessageSelection {
        IpSetter<InternalRequestSendTalkMessage> talk(String content);
        IpSetter<InternalRequestSendFileMessage> file(String fileName);
        IpSetter<InternalRequestSendFullFileMessage> fullFile(String fileName);
        InternalRequestSendChunkMessage.ByteArraySetter chunk(int seqNumber);
        IpSetter<InternalRequestSendEndMessage> end(String fileHash);
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
        public IpSetter<InternalRequestSendFullFileMessage> fullFile(String fileName) {
            return InternalRequestSendFullFileMessage.create(clazz, fileName);
        }

        @Override
        public InternalRequestSendChunkMessage.ByteArraySetter chunk(int seqNumber) {
            return InternalRequestSendChunkMessage.create(clazz, seqNumber);
        }

        @Override
        public IpSetter<InternalRequestSendEndMessage> end(String fileHash) {
            return InternalRequestSendEndMessage.create(clazz, fileHash);
        }

        @Override
        public IpSetter<InternalRequestSendAckMessage> ack(int messageId) {
            return InternalRequestSendAckMessage.create(clazz, messageId);
        }

        @Override
        public InternalRequestSendNAckMessage.StringSetter nAck(int messageId) {
            return InternalRequestSendNAckMessage.create(clazz, messageId);
        }
    }

    public static Builder build(Class<?> clazz) {
        return new Builder(clazz);
    }

    // ****************************************************************************************************
    // Abstract Builder pattern for InternalRequestSendMessage subclasses

    public interface IpSetter<T extends InternalRequestMessage> {
        PortSetter<T> to(String destinationIp) throws UnknownHostException;
        PortSetter<T> to(InetAddress destinationIp);
    }
    public interface PortSetter<T extends InternalRequestMessage> {
        T at(int port);
    }
 
    protected static abstract class IpBuilder<T extends InternalRequestMessage>
                                implements IpSetter<T>, PortSetter<T> {
        protected InetAddress destinationIp;
        protected int port;

        @Override
        public final PortSetter<T> to(String destinationIp) throws UnknownHostException {
            this.destinationIp = InetAddress.getByName(destinationIp);
            return this;
        }

        @Override
        public final PortSetter<T> to(InetAddress destinationIp) {
            this.destinationIp = destinationIp;
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
