package interfaces.visitors;

import messages.foreign.ForeignMessage;
import messages.internal.InternalMessage;

public interface MessageVisitor {
    void visit(ForeignMessage message);
    void visit(InternalMessage message);
}
