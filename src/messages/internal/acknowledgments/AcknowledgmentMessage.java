package messages.internal.acknowledgments;

import java.net.InetAddress;

import messages.internal.InternalMessage;

public abstract class AcknowledgmentMessage extends InternalMessage {
    protected int messageId;
    protected InetAddress sourceIp;

    public InetAddress getSourceIp() {
        return sourceIp;
    }
    
    // **************************************************************************************************************
    // Abstract Builder pattern for InternalReceivedMessage subclasses

    public interface IpSetter<T extends AcknowledgmentMessage> {
        T from(InetAddress sourceIp);
    }

    protected static abstract class IpBuilder<T extends AcknowledgmentMessage> implements IpSetter<T> {
        protected InetAddress sourceIp;

        @Override
        public final T from(InetAddress sourceIp) {
            this.sourceIp = sourceIp;
            return self();
        }

        protected abstract T self();
    } 
}
