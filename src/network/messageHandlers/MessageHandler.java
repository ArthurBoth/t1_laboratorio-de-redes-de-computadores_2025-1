package network.messageHandlers;

import java.util.concurrent.BlockingQueue;

import interfaces.visitors.MessageVisitor;
import messages.ThreadMessage;
import messages.foreign.ForeignMessage;
import messages.internal.InternalMessage;

public class MessageHandler implements MessageVisitor {
    private BlockingQueue<ThreadMessage> ioSenderQueue;
    private BlockingQueue<ThreadMessage> udpSenderQueue;

    private ForeignMessageHandler foreignManager;
    private InternalMessageHandler internalManager;

    // ****************************************************************************************************************
    // Visitor pattern for MessageManager

    @Override
    public void visit(ForeignMessage message) {
        message.accept(foreignManager);
    }
    @Override
    public void visit(InternalMessage message) {
        message.accept(internalManager);
    }   
}
