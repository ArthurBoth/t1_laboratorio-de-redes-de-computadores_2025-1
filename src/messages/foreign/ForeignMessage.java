package messages.foreign;

import java.net.InetAddress;

import interfaces.visitors.EncoderVisitor;
import interfaces.visitors.MessageVisitor;
import interfaces.visitors.foreign.ForeignVisitor;
import messages.ThreadMessage;
import messages.foreign.ForeignChunkMessage.ByteArraySetter;
import messages.foreign.ForeignFileMessage.LongSetter;
import messages.foreign.ForeignNAckMessage.StringSetter;

public abstract class ForeignMessage extends ThreadMessage {
    // ****************************************************************************************************
    // The ForeignMessage class is the base class for all external messages in the system.
    //    It should be used to create messages that will be sent over the network.
    // ****************************************************************************************************
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

    public void accept(ForeignVisitor visitor) {
        visitor.ack(this);
    }

    // ****************************************************************************************************
    // Builder pattern for ForeignMessage

    public interface IdMessageSeleciton {
        ForeignTalkMessage.IpBuilder<ForeignTalkMessage> talk(String content);
        ForeignFileMessage.LongSetter file(String fileName);
        ForeignChunkMessage.ByteArraySetter chunk(int chunkNumber);
        ForeignEndMessage.IpBuilder<ForeignEndMessage> end(String fileHash);
    }

    private static class IdBuilder implements IdMessageSeleciton {
        private int messageId;

        private IdBuilder(int messageId) {
            this.messageId = messageId;
        }

        @Override
        public IpBuilder<ForeignTalkMessage> talk(String content) {
            return ForeignTalkMessage.create(messageId, content);
        }

        @Override
        public LongSetter file(String fileName) {
            return ForeignFileMessage.create(messageId, fileName);
        }

        @Override
        public ByteArraySetter chunk(int chunkNumber) {
            return ForeignChunkMessage.create(messageId, chunkNumber);
        }

        @Override
        public IpBuilder<ForeignEndMessage> end(String fileHash) {
            return ForeignEndMessage.create(messageId, fileHash);
        }
    }

    public interface IdlessMessageSeleciton {
        ForeignHeartbeatMessage heartbeat();
        ForeignAckMessage.IpBuilder<ForeignAckMessage> ack(int ackkedMessageId);
        ForeignNAckMessage.StringSetter nAck(int nonAckkedMessageId);
    }

    private static class IdlessBuilder implements IdlessMessageSeleciton {
        @Override
        public ForeignHeartbeatMessage heartbeat() {
            return ForeignHeartbeatMessage.create();
        }

        @Override
        public IpBuilder<ForeignAckMessage> ack(int ackkedMessageId) {
            return ForeignAckMessage.create(ackkedMessageId);
        }

        @Override
        public StringSetter nAck(int nonAckkedMessageId) {
            return ForeignNAckMessage.create(nonAckkedMessageId);
        }
    }

    public static IdMessageSeleciton instance(int messageId) {
        return new IdBuilder(messageId);
    }

    public static IdlessBuilder instance() {
        return  new IdlessBuilder();
    }

    // ****************************************************************************************************
    // Abstract Builder pattern for ForeignMessage subclasses

    public interface IpSetter<T extends ForeignMessage> {
        T to(InetAddress destinationIp);
    }

    public static abstract class IpBuilder<T extends ForeignMessage> implements IpSetter<T> {
        protected InetAddress destinationIp;
        protected int port;

        @Override
        public final T to(InetAddress destinationIp) {
            this.destinationIp = destinationIp;
            return self();
        }

        protected abstract T self();
    }
}
