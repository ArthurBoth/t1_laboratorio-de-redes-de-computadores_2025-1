package interfaces.visitors;

import messages.internal.InternalMessage;
import messages.internal.received.InternalReceivedMessage;
import messages.internal.received.InternalReceivedTalkMessage;
import messages.internal.requested.send.InternalRequestSendMessage;
import messages.internal.requested.send.InternalRequestSendTalkMessage;

public interface LoggerVisitor {
    void visit(InternalMessage message);
    void visit(InternalReceivedMessage message);
    void visit(InternalReceivedTalkMessage message);
    void visit(InternalRequestSendMessage message);
    void visit(InternalRequestSendTalkMessage message);
}
