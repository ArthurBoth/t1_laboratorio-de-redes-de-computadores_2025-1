package interfaces.visitors;

import messages.internal.received.InternalReceivedChunkMessage;
import messages.internal.received.InternalReceivedEndMessage;
import messages.internal.received.InternalReceivedFileMessage;

public interface FileMessageVisitor {
    void visit(InternalReceivedFileMessage message);
    void visit(InternalReceivedChunkMessage message);
    void visit(InternalReceivedEndMessage message);
}
