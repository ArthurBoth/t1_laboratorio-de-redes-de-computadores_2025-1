package network.messageHandlers;

import interfaces.visitors.InternalMessageVisitor;
import messages.internal.receivedMessages.InternalReceivedAckMessage;
import messages.internal.receivedMessages.InternalReceivedChunkMessage;
import messages.internal.receivedMessages.InternalReceivedEndMessage;
import messages.internal.receivedMessages.InternalReceivedFileMessage;
import messages.internal.receivedMessages.InternalReceivedHeartbeatMessage;
import messages.internal.receivedMessages.InternalReceivedNAckMessage;
import messages.internal.receivedMessages.InternalReceivedTalkMessage;
import messages.internal.receivedMessages.InternalReceivedUnsupportedMessage;
import messages.internal.sentMessages.InternalExitMessage;
import messages.internal.sentMessages.InternalSentAckMessage;
import messages.internal.sentMessages.InternalSentFileMessage;
import messages.internal.sentMessages.InternalSentNAckMessage;
import messages.internal.sentMessages.InternalSentTalkMessage;

public class InternalMessageHandler implements InternalMessageVisitor {

    @Override
    public void visit(InternalSentTalkMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalSentFileMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalSentAckMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalSentNAckMessage message) {
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
    public void visit(InternalExitMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
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
