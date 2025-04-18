package interfaces.visitors;

import network.messages.foreign.ForeignAck;
import network.messages.foreign.ForeignNAck;

public interface AcknowledgementVisitor {
    void visit(ForeignAck visitable);
    void visit(ForeignNAck visitable);
}
