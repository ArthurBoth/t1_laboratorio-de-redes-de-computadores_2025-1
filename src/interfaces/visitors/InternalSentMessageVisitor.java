package interfaces.visitors;

import messages.internal.sentMessages.InternalExitMessage;
import messages.internal.sentMessages.InternalSentAckMessage;
import messages.internal.sentMessages.InternalSentFileMessage;
import messages.internal.sentMessages.InternalSentNAckMessage;
import messages.internal.sentMessages.InternalSentTalkMessage;

public interface InternalSentMessageVisitor extends MessageVisitor {
    void visit(InternalExitMessage message);
    void visit(InternalSentTalkMessage message);
    void visit(InternalSentFileMessage message);
    void visit(InternalSentAckMessage message);
    void visit(InternalSentNAckMessage message);
}
