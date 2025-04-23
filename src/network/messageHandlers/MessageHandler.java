package network.messageHandlers;

import java.util.concurrent.BlockingQueue;

import interfaces.visitors.internal.InternalReceivedMessageVisitor;
import interfaces.visitors.internal.InternalRequestMessageVisitor;
import messages.foreign.ForeignMessage;
import messages.internal.InternalMessage;
import messages.internal.received.*;
import messages.internal.requested.*;


public class MessageHandler implements InternalReceivedMessageVisitor, InternalRequestMessageVisitor {
    private final BlockingQueue<ForeignMessage> UDP_SENDER_QUEUE;
    private final BlockingQueue<InternalMessage> IO_SENDER_QUEUE;

    public MessageHandler(
        BlockingQueue<ForeignMessage> udpSenderQueue,
        BlockingQueue<InternalMessage> ioSenderQueue
    ) {
        UDP_SENDER_QUEUE = udpSenderQueue;
        IO_SENDER_QUEUE  = ioSenderQueue;
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalReceivedMessageVisitor

    @Override
    public void visit(InternalReceivedHeartbeatMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalReceivedTalkMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalReceivedFileMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalReceivedChunkMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalReceivedEndMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalReceivedAckMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalReceivedNAckMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalReceivedUnsupportedMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    // ****************************************************************************************************
    // Visitor pattern for InternalRequestMessageVisitor

    @Override
    public void visit(InternalRequestExitMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalRequestSendTalkMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalRequestSendFileMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalRequestSendAckMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(InternalRequestSendNAckMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }
}
