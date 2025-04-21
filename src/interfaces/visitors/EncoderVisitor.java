package interfaces.visitors;

import messages.foreign.ForeignAckMessage;
import messages.foreign.ForeignChunkMessage;
import messages.foreign.ForeignEndMessage;
import messages.foreign.ForeignFileMessage;
import messages.foreign.ForeignHeartbeatMessage;
import messages.foreign.ForeignNAckMessage;
import messages.foreign.ForeignTalkMessage;

public interface EncoderVisitor {
    byte[] encode(ForeignAckMessage message);
    byte[] encode(ForeignChunkMessage message);
    byte[] encode(ForeignEndMessage message);
    byte[] encode(ForeignFileMessage message);
    byte[] encode(ForeignHeartbeatMessage message);
    byte[] encode(ForeignNAckMessage message);
    byte[] encode(ForeignTalkMessage message);
}
