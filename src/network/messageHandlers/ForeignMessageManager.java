package network.messageHandlers;

import interfaces.visitors.ForeignMessageVisitor;
import messages.foreign.ForeignAckMessage;
import messages.foreign.ForeignChunkMessage;
import messages.foreign.ForeignEndMessage;
import messages.foreign.ForeignFileMessage;
import messages.foreign.ForeignHeartbeatMessage;
import messages.foreign.ForeignNAckMessage;
import messages.foreign.ForeignTalkMessage;

public class ForeignMessageManager implements ForeignMessageVisitor {

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
