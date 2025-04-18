package interfaces.visitors;

import network.messages.foreign.ForeignAck;
import network.messages.foreign.ForeignChunk;
import network.messages.foreign.ForeignEnd;
import network.messages.foreign.ForeignFile;
import network.messages.foreign.ForeignHeartbeat;
import network.messages.foreign.ForeignNAck;
import network.messages.foreign.ForeignResponseWrapper;
import network.messages.foreign.ForeignTalk;

public interface ForeignMessageVisitor {
    ForeignResponseWrapper visit(ForeignHeartbeat visitable);
    ForeignResponseWrapper visit(ForeignTalk visitable);
    ForeignResponseWrapper visit(ForeignFile visitable);
    ForeignResponseWrapper visit(ForeignChunk visitable);
    ForeignResponseWrapper visit(ForeignEnd visitable);
    ForeignResponseWrapper visit(ForeignAck visitable);
    ForeignResponseWrapper visit(ForeignNAck visitable);
}
