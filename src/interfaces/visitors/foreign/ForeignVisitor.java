package interfaces.visitors.foreign;

import messages.foreign.ForeignFileMessage;
import messages.foreign.ForeignMessage;

public interface ForeignVisitor {
    void ack(ForeignMessage message);
    void ack(ForeignFileMessage message);
}
