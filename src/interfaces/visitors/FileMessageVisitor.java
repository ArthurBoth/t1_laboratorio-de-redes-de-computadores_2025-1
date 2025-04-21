package interfaces.visitors;

import messages.internal.receivedMessages.InternalReceivedChunkMessage;
import messages.internal.receivedMessages.InternalReceivedEndMessage;
import messages.internal.receivedMessages.InternalReceivedFileMessage;

public interface FileMessageVisitor {
    void visit(InternalReceivedFileMessage message);
    void visit(InternalReceivedChunkMessage message);
    void visit(InternalReceivedEndMessage message);
}
