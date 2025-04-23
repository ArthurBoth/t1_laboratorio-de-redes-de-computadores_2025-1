package interfaces.visitors.internal;

import messages.internal.received.InternalReceivedAckMessage;
import messages.internal.received.InternalReceivedChunkMessage;
import messages.internal.received.InternalReceivedEndMessage;
import messages.internal.received.InternalReceivedFileMessage;
import messages.internal.received.InternalReceivedHeartbeatMessage;
import messages.internal.received.InternalReceivedNAckMessage;
import messages.internal.received.InternalReceivedTalkMessage;
import messages.internal.received.InternalReceivedUnsupportedMessage;

public interface InternalReceivedMessageVisitor {
    void visit(InternalReceivedHeartbeatMessage message);
    void visit(InternalReceivedTalkMessage message);
    void visit(InternalReceivedFileMessage message);
    void visit(InternalReceivedChunkMessage message);
    void visit(InternalReceivedEndMessage message);
    void visit(InternalReceivedAckMessage message);
    void visit(InternalReceivedNAckMessage message);
    void visit(InternalReceivedUnsupportedMessage message);
}
