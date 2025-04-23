package messages.foreign;

import java.net.InetAddress;

import interfaces.visitors.EncoderVisitor;
import interfaces.visitors.MessageVisitor;
import messages.ThreadMessage;
import messages.foreign.ForeignChunkMessage.ByteArraySetter;
import messages.foreign.ForeignFileMessage.LongSetter;
import messages.foreign.ForeignNAckMessage.StringSetter;

public abstract class ForeignMessage extends ThreadMessage {
    // ****************************************************************************************************
    // The ForeignMessage class is the base class for all external messages in the system.
    //    It should be used to create messages that will be sent over the network.
    // ****************************************************************************************************
    private static int messageIdCounter = 0;

    protected InetAddress destinationIp;

    public final InetAddress getDestinationIp() {
        return destinationIp;
    }
    
    // ****************************************************************************************************
    // Visitor pattern for ForeignMessage

    public abstract byte[] encode(EncoderVisitor visitor);

    @Override
    public void accept(MessageVisitor visitor) {
        visitor.visit(this);
    }

    // ****************************************************************************************************
    // Builder pattern for ForeignMessage

    public interface MessageSeleciton {
        ForeignHeartbeatMessage.IpSetter<ForeignHeartbeatMessage> heartbeat();
        ForeignTalkMessage.IpSetter<ForeignTalkMessage> talk(String content);
        ForeignFileMessage.LongSetter file(String fileName);
        ForeignChunkMessage.ByteArraySetter chunk(int chunkNumber);
        ForeignEndMessage.IpSetter<ForeignEndMessage> end(String fileHash);
        ForeignAckMessage.IpSetter<ForeignAckMessage> ack(int ackkedMessageId);
        ForeignNAckMessage.StringSetter nAck(int nonAckkedMessageId);
    }

    private static class Builder implements MessageSeleciton {
        private Class<?> clazz;

        private Builder(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public IpSetter<ForeignHeartbeatMessage> heartbeat() {
            return ForeignHeartbeatMessage.create(clazz); 
        }

        @Override
        public IpSetter<ForeignTalkMessage> talk(String content) {
            return ForeignTalkMessage.create(clazz, messageIdCounter++, content); 
        }

        @Override
        public LongSetter file(String fileName) {
            return ForeignFileMessage.create(clazz, messageIdCounter++, fileName); 
        }

        @Override
        public ByteArraySetter chunk(int chunkNumber) {
            return ForeignChunkMessage.create(clazz, messageIdCounter++, chunkNumber); 
        }

        @Override
        public IpSetter<ForeignEndMessage> end(String fileHash) {
            return ForeignEndMessage.create(clazz, messageIdCounter++, fileHash); 
        }

        @Override
        public IpSetter<ForeignAckMessage> ack(int ackkedMessageId) {
            return ForeignAckMessage.create(clazz, ackkedMessageId); 
        }

        @Override
        public StringSetter nAck(int nonAckkedMessageId) {
            return ForeignNAckMessage.create(clazz, nonAckkedMessageId);
        }

    }

    public static MessageSeleciton instance(Class<?> clazz) {
        return new Builder(clazz);
    }

    // ****************************************************************************************************
    // Abstract Builder pattern for ForeignMessage subclasses

    public interface IpSetter<T extends ForeignMessage> {
        T to(InetAddress destinationIp);
    }

    protected static abstract class IpBuilder<T extends ForeignMessage> implements IpSetter<T> {
        protected InetAddress destinationIp;

        @Override
        public final T to(InetAddress destinationIp) {
            this.destinationIp = destinationIp;
            return self();
        }

        protected abstract T self();
    }
}
