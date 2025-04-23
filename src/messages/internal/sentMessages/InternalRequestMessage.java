package messages.internal.sentMessages;

import java.net.InetAddress;
import java.net.UnknownHostException;

import interfaces.visitors.InternalSentMessageVisitor;
import messages.internal.InternalMessage;
import messages.internal.sentMessages.InternalRequestSendNAckMessage.StringSetter;

public abstract class InternalRequestMessage extends InternalMessage {
    // ****************************************************************************************************
    // The InternalSentMessage class is the base class for messages that will, eventually, be sent over the 
    // network.
    //    It should be used to create messages that won't be sent over the network.
    // ****************************************************************************************************
    
    protected InetAddress destinationIp;

    public InetAddress getDestinationIp() {
        return destinationIp;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalSentMessage

    public abstract void accept(InternalSentMessageVisitor visitor); 

    // ****************************************************************************************************
    // Builder pattern for InternalSentMessage

    public interface MessageSelection {
        InternalRequestExitMessage exit();
        IpSetter<InternalRequestTalkMessage> talk(String content);
        IpSetter<InternalRequestSendFileMessage> file(String fileName);
        IpSetter<InternalRequestSendAckMessage> ack(int messageId);
        InternalRequestSendNAckMessage.StringSetter nAck(int messageId);
    }

    private static final class Builder implements MessageSelection {
        private Class<?> clazz;

        private Builder(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public InternalRequestExitMessage exit() {
            return InternalRequestExitMessage.build(clazz);
        }

        @Override
        public IpSetter<InternalRequestTalkMessage> talk(String content) {
            return InternalRequestTalkMessage.create(clazz, content);
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

    public static Builder create(Class<?> clazz) {
        return new Builder(clazz);
    }

    // ****************************************************************************************************
    // Abstract Builder pattern for InternalSentMessage subclasses

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
