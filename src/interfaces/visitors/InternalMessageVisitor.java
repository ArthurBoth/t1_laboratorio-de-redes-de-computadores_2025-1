package interfaces.visitors;

import messages.internal.receivedMessages.InternalReceivedAckMessage;
import messages.internal.receivedMessages.InternalReceivedChunkMessage;
import messages.internal.receivedMessages.InternalReceivedEndMessage;
import messages.internal.receivedMessages.InternalReceivedFileMessage;
import messages.internal.receivedMessages.InternalReceivedHeartbeatMessage;
import messages.internal.receivedMessages.InternalReceivedNAckMessage;
import messages.internal.receivedMessages.InternalReceivedTalkMessage;
import messages.internal.receivedMessages.InternalReceivedUnsupportedMessage;
import messages.internal.sentMessages.InternalRequestSendAckMessage;
import messages.internal.sentMessages.InternalRequestExitMessage;
import messages.internal.sentMessages.InternalRequestSendFileMessage;
import messages.internal.sentMessages.InternalRequestSendNAckMessage;
import messages.internal.sentMessages.InternalRequestTalkMessage;

public interface InternalMessageVisitor {
    // Control
    void visit(InternalRequestExitMessage message);

    // Internal sent messages
    void visit(InternalRequestTalkMessage message);
    void visit(InternalRequestSendFileMessage message);
    void visit(InternalRequestSendAckMessage message);
    void visit(InternalRequestSendNAckMessage message);
    
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
