package interfaces.visitors;

import messages.foreign.ForeignMessage;
import messages.foreign.ForeignTalkMessage;
import messages.internal.InternalMessage;

public interface LoggerVisitor {
    void visit(ForeignMessage message);
    void visit(ForeignTalkMessage message);
    void visit(InternalMessage message);
}
