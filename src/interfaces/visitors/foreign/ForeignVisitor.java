package interfaces.visitors.foreign;

import messages.foreign.ForeignChunkMessage;
import messages.foreign.ForeignEndMessage;
import messages.foreign.ForeignFileMessage;
import messages.foreign.ForeignMessage;

public interface ForeignVisitor {
    void ack(ForeignMessage message);
    void ack(ForeignFileMessage message);
    void ack(ForeignChunkMessage message);
    void ack(ForeignEndMessage message);

    void nack(ForeignMessage message);
    void nack(ForeignFileMessage message);
    void nack(ForeignEndMessage message);
}
