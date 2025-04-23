package interfaces.visitors.internal;

import messages.internal.received.InternalReceivedMessage;
import messages.internal.requested.InternalRequestMessage;

public interface InternalMessageVisitor {
    void visit(InternalRequestMessage message);
    void visit(InternalReceivedMessage message);
}
