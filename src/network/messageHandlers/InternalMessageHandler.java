package network.messageHandlers;

import java.util.concurrent.BlockingQueue;

import interfaces.visitors.InternalMessageVisitor;
import messages.ThreadMessage;
import messages.internal.receivedMessages.*;
import messages.internal.sentMessages.*;
import utils.Exceptions.EndExecutionException;

public class InternalMessageHandler implements InternalMessageVisitor {
    private BlockingQueue<ThreadMessage> ioSenderQueue;

    public InternalMessageHandler(BlockingQueue<ThreadMessage> ioSenderQueue) {
        this.ioSenderQueue = ioSenderQueue;
    }

    @Override
    public void visit(InternalRequestTalkMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalRequestSendFileMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalRequestSendAckMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalRequestSendNAckMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalReceivedChunkMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalReceivedFileMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalReceivedTalkMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalReceivedEndMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalRequestExitMessage message) {
        throw new EndExecutionException();
    }

    @Override
    public void visit(InternalReceivedHeartbeatMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalReceivedUnsupportedMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalReceivedAckMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalReceivedNAckMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }
    
    
}
