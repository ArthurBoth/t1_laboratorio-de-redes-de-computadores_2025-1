package interfaces.visitors;

import messages.internal.receivedMessages.InternalReceivedAckMessage;
import messages.internal.receivedMessages.InternalReceivedChunkMessage;
import messages.internal.receivedMessages.InternalReceivedEndMessage;
import messages.internal.receivedMessages.InternalReceivedFileMessage;
import messages.internal.receivedMessages.InternalReceivedHeartbeatMessage;
import messages.internal.receivedMessages.InternalReceivedNAckMessage;
import messages.internal.receivedMessages.InternalReceivedTalkMessage;
import messages.internal.receivedMessages.InternalReceivedUnsupportedMessage;
import messages.internal.sentMessages.InternalExitMessage;
import messages.internal.sentMessages.InternalSentAckMessage;
import messages.internal.sentMessages.InternalSentFileMessage;
import messages.internal.sentMessages.InternalSentNAckMessage;
import messages.internal.sentMessages.InternalSentTalkMessage;

public interface InternalMessageVisitor {
    // Control
    void visit(InternalExitMessage message);

    // Internal sent messages
    void visit(InternalSentTalkMessage message);
    void visit(InternalSentFileMessage message);
    void visit(InternalSentAckMessage message);
    void visit(InternalSentNAckMessage message);
    
    // Internal received messages
    void visit(InternalReceivedHeartbeatMessage message);
    void visit(InternalReceivedTalkMessage message);
    void visit(InternalReceivedFileMessage message);
    void visit(InternalReceivedChunkMessage message);
    void visit(InternalReceivedEndMessage message);
    void visit(InternalReceivedAckMessage message);
    void visit(InternalReceivedNAckMessage message);
    void visit(InternalReceivedUnsupportedMessage message);
}
