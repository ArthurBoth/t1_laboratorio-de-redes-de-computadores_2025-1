package io;

import interfaces.visitors.internal.InternalMessageVisitor;
import io.files.FileManager;
import messages.internal.received.InternalReceivedMessage;
import messages.internal.requested.InternalRequestMessage;

public class IoHandler implements InternalMessageVisitor {
    private IoManager manager;
    private FileManager fileManager;

    public IoHandler(IoManager manager) {
        this.manager = manager;
    }

    @Override
    public void visit(InternalRequestMessage message) {
        message.accept(manager);
    }

    @Override
    public void visit(InternalReceivedMessage message) {
        message.accept(fileManager);
    }
}
