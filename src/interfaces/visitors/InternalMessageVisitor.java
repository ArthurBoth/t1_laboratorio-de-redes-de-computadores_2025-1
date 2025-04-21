package interfaces.visitors;

import messages.internal.acknowledgments.AckMessage;
import messages.internal.acknowledgments.NAckMessage;
import messages.internal.receivedMessages.InternalReceivedChunkMessage;
import messages.internal.receivedMessages.InternalReceivedEndMessage;
import messages.internal.receivedMessages.InternalReceivedFileMessage;
import messages.internal.receivedMessages.InternalReceivedTalkMessage;
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
    void visit(InternalReceivedChunkMessage message);
    void visit(InternalReceivedFileMessage message);
    void visit(InternalReceivedTalkMessage message);
    void visit(InternalReceivedEndMessage message);
    void visit(AckMessage message);
    void visit(NAckMessage message);
}
