package network.messageHandlers;

import java.util.concurrent.BlockingQueue;

import interfaces.visitors.ForeignMessageVisitor;
import messages.ThreadMessage;
import messages.foreign.*;

public class ForeignMessageHandler implements ForeignMessageVisitor {
    private BlockingQueue<ForeignMessage> udpSenderQueue;
    private BlockingQueue<ThreadMessage> ioSenderQueue;

    public ForeignMessageHandler(BlockingQueue<ForeignMessage> udpSenderQueue, BlockingQueue<ThreadMessage> ioSenderQueue) {
        this.udpSenderQueue = udpSenderQueue;
        this.ioSenderQueue  = ioSenderQueue;
    }

    @Override
    public void visit(ForeignHeartbeatMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(ForeignTalkMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(ForeignFileMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(ForeignChunkMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(ForeignEndMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(ForeignAckMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(ForeignNAckMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }
    
}
