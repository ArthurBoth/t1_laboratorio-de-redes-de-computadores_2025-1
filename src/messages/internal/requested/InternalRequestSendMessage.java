package messages.internal.requested;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class InternalRequestSendMessage extends InternalRequestMessage {
    protected InetAddress destinationIp;

    public InetAddress getDestinationIp() {
        return destinationIp;
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
