package interfaces.visitors;

import messages.foreign.ForeignMessage;
import messages.foreign.ForeignTalkMessage;
import messages.internal.InternalMessage;
import messages.internal.receivedMessages.InternalReceivedMessage;
import messages.internal.receivedMessages.InternalReceivedTalkMessage;

public interface LoggerVisitor {
    void visit(ForeignMessage message);
    void visit(ForeignTalkMessage message);
    void visit(InternalMessage message);
    void visit(InternalReceivedMessage message);
    void visit(InternalReceivedTalkMessage message);
}
