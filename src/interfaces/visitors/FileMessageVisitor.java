package interfaces.visitors;

import messages.internal.received.InternalReceivedChunkMessage;
import messages.internal.received.InternalReceivedEndMessage;
import messages.internal.received.InternalReceivedFileMessage;
import messages.internal.requested.send.InternalRequestSendFileMessage;
import messages.internal.requested.send.InternalRequestSendFullFileMessage;

public interface FileMessageVisitor {
    void visit(InternalReceivedFileMessage message);
    void visit(InternalReceivedChunkMessage message);
    void visit(InternalReceivedEndMessage message);
    void visit(InternalRequestSendFileMessage message);
    void visit(InternalRequestSendFullFileMessage message);
}
