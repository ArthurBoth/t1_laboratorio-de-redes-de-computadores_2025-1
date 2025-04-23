package network.messageHandlers;

import java.util.concurrent.BlockingQueue;

import interfaces.visitors.MessageVisitor;
import messages.ThreadMessage;
import messages.foreign.ForeignMessage;
import messages.internal.InternalMessage;

public class MessageHandler implements MessageVisitor {
    private BlockingQueue<ThreadMessage> ioSenderQueue;

    private ForeignMessageHandler foreignManager;
    private InternalMessageHandler internalManager;

    public MessageHandler(BlockingQueue<ForeignMessage> udpSenderQueue, BlockingQueue<ThreadMessage> ioSenderQueue) {
        this.ioSenderQueue  = ioSenderQueue;

        foreignManager  = new ForeignMessageHandler(udpSenderQueue, ioSenderQueue);
        internalManager = new InternalMessageHandler(ioSenderQueue);
    }

    // ****************************************************************************************************
    // Visitor pattern for MessageManager

    @Override
    public void visit(ForeignMessage message) {
        ioSenderQueue.offer(message); // Sends to I/O Manager to log the received
        message.accept(foreignManager);
    }
    @Override
    public void visit(InternalMessage message) {
        message.accept(internalManager);
    }   
}
