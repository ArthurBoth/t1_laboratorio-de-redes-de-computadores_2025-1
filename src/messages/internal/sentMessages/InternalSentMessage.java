package messages.internal.sentMessages;

import java.net.InetAddress;

import messages.internal.InternalMessage;

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
    // Builder pattern for InternalSentMessage

    public interface MessageSelection {
        IpSetter<InternalSentTalkMessage> sendTalk(String content);
        IpSetter<InternalSentFileMessage> sendFile(String fileName);
    }

    private static final class Builder implements MessageSelection {
        private Class<?> clazz;

        private Builder(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public IpSetter<InternalSentFileMessage> sendFile(String fileName) {
            return InternalSentFileMessage.create(clazz, fileName);
        }

        @Override
        public IpSetter<InternalSentTalkMessage> sendTalk(String content) {
            return InternalSentTalkMessage.create(clazz, content);
        }
    }

    public static Builder create(Class<?> clazz) {
        return new Builder(clazz);
    }

    // **************************************************************************************************************
    // Abstract Builder pattern for InternalSentMessage subclasses

    public interface IpSetter<T extends InternalSentMessage> {
        T to(InetAddress destinationIp);
    }
 
    protected static abstract class IpBuilder<T extends InternalSentMessage> implements IpSetter<T> {
        protected InetAddress destinationIp;

        @Override
        public final T to(InetAddress destinationIp) {
            this.destinationIp = destinationIp;
            return self();
        }

        protected abstract T self();
    }
}
