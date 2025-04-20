package interfaces.visitors;

import messages.foreign.ForeignEndMessage;
import messages.foreign.ForeignFileMessage;
import messages.foreign.ForeignHeartbeatMessage;
import messages.foreign.ForeignNAckMessage;
import messages.foreign.ForeignTalkMessage;
import messages.foreign.ForeignAckMessage;
import messages.foreign.ForeignChunkMessage;

public interface ForeignMessageVisitor {
    void visit(ForeignHeartbeatMessage message);
    void visit(ForeignTalkMessage message);
    void visit(ForeignFileMessage message);
    void visit(ForeignChunkMessage message);
    void visit(ForeignEndMessage message);
    void visit(ForeignAckMessage message);
    void visit(ForeignNAckMessage message);
}
