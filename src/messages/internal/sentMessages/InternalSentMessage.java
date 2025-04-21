package messages.internal.sentMessages;

import java.net.InetAddress;
import java.net.UnknownHostException;

import interfaces.visitors.InternalSentMessageVisitor;
import messages.internal.InternalMessage;
import messages.internal.sentMessages.InternalSentNAckMessage.StringSetter;

public abstract class InternalSentMessage extends InternalMessage {
    // **************************************************************************************************************
    // The InternalSentMessage class is the base class for messages that will, eventually, be sent over the 
    // network.
    //    It should be used to create messages that won't be sent over the network.
    // **************************************************************************************************************
    
    protected InetAddress destinationIp;

    public InetAddress getDestinationIp() {
        return destinationIp;
    }

    // **************************************************************************************************************
    // Visitor pattern for InternalSentMessage

    public abstract void accept(InternalSentMessageVisitor visitor); 

    // **************************************************************************************************************
    // Builder pattern for InternalSentMessage

    public interface MessageSelection {
        InternalExitMessage exit();
        IpSetter<InternalSentTalkMessage> talk(String content);
        IpSetter<InternalSentFileMessage> file(String fileName);
        IpSetter<InternalSentAckMessage> ack(int messageId);
        InternalSentNAckMessage.StringSetter nAck(int messageId);
    }

    private static final class Builder implements MessageSelection {
        private Class<?> clazz;

        private Builder(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public InternalExitMessage exit() {
            return InternalExitMessage.build(clazz);
        }

        @Override
        public IpSetter<InternalSentTalkMessage> talk(String content) {
            return InternalSentTalkMessage.create(clazz, content);
        }

        @Override
        public IpSetter<InternalSentFileMessage> file(String fileName) {
            return InternalSentFileMessage.create(clazz, fileName);
        }

        @Override
        public IpSetter<InternalSentAckMessage> ack(int messageId) {
            return InternalSentAckMessage.create(clazz, messageId);
        }

        @Override
        public StringSetter nAck(int messageId) {
            return InternalSentNAckMessage.create(clazz, messageId);
        }
    }

    public static Builder create(Class<?> clazz) {
        return new Builder(clazz);
    }

    // **************************************************************************************************************
    // Abstract Builder pattern for InternalSentMessage subclasses

    public interface IpSetter<T extends InternalSentMessage> {
        T to(String destinationIp) throws UnknownHostException;
        T to(InetAddress destinationIp);
    }
 
    protected static abstract class IpBuilder<T extends InternalSentMessage> implements IpSetter<T> {
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
