package interfaces.visitors.internal;

import messages.internal.requested.InternalRequestExitMessage;
import messages.internal.requested.InternalRequestResendMessage;
import messages.internal.requested.send.InternalRequestSendAckMessage;
import messages.internal.requested.send.InternalRequestSendFileMessage;
import messages.internal.requested.send.InternalRequestSendNAckMessage;
import messages.internal.requested.send.InternalRequestSendTalkMessage;

public interface InternalRequestMessageVisitor {
    void visit(InternalRequestExitMessage message);
    void visit(InternalRequestResendMessage message);
    void visit(InternalRequestSendTalkMessage message);
    void visit(InternalRequestSendFileMessage message);
    void visit(InternalRequestSendAckMessage message);
    void visit(InternalRequestSendNAckMessage message);
}
