package interfaces.visitors.internal;

import messages.internal.requested.*;
import messages.internal.requested.send.*;

public interface InternalRequestMessageVisitor {
    void visit(InternalRequestExitMessage message);
    void visit(InternalRequestUpdateSendStatusMessage message);
    void visit(InternalRequestDisplaySuccessMessage message);
    void visit(InternalRequestDisplayFailureMessage message);
    void visit(InternalRequestAbortFileSendingMessage message);

    void visit(InternalRequestResendMessage message);
    void visit(InternalRequestSendTalkMessage message);
    void visit(InternalRequestSendFileMessage message);
    void visit(InternalRequestSendChunkMessage message);
    void visit(InternalRequestSendEndMessage message);
    void visit(InternalRequestSendAckMessage message);
    void visit(InternalRequestSendNAckMessage message);
    void visit(InternalRequestSendFullFileMessage message);
}
