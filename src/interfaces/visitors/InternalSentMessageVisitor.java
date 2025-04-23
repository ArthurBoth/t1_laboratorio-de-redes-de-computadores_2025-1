package interfaces.visitors;

import messages.internal.sentMessages.InternalRequestSendAckMessage;
import messages.internal.sentMessages.InternalRequestExitMessage;
import messages.internal.sentMessages.InternalRequestSendFileMessage;
import messages.internal.sentMessages.InternalRequestSendNAckMessage;
import messages.internal.sentMessages.InternalRequestTalkMessage;

public interface InternalSentMessageVisitor extends MessageVisitor {
    void visit(InternalRequestExitMessage message);
    void visit(InternalRequestTalkMessage message);
    void visit(InternalRequestSendFileMessage message);
    void visit(InternalRequestSendAckMessage message);
    void visit(InternalRequestSendNAckMessage message);
}
