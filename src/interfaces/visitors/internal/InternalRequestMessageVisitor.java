package interfaces.visitors.internal;

import messages.internal.requested.InternalRequestExitMessage;
import messages.internal.requested.InternalRequestSendAckMessage;
import messages.internal.requested.InternalRequestSendFileMessage;
import messages.internal.requested.InternalRequestSendNAckMessage;
import messages.internal.requested.InternalRequestSendTalkMessage;

public interface InternalRequestMessageVisitor {
    void visit(InternalRequestExitMessage message);
    void visit(InternalRequestSendTalkMessage message);
    void visit(InternalRequestSendFileMessage message);
    void visit(InternalRequestSendAckMessage message);
    void visit(InternalRequestSendNAckMessage message);
}
