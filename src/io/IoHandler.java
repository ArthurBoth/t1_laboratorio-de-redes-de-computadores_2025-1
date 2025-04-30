package io;

import interfaces.visitors.FileMessageVisitor;
import interfaces.visitors.internal.InternalMessageVisitor;
import io.files.FileManager;
import messages.internal.received.InternalReceivedFileRelated;
import messages.internal.received.InternalReceivedMessage;
import messages.internal.requested.InternalRequestMessage;

public class IoHandler implements InternalMessageVisitor {
    private IoManager manager;
    private FileManager fileManager;

    public IoHandler(IoManager manager, FileManager fileManager) {
        this.manager     = manager;
        this.fileManager = fileManager;
    }

    @Override
    public void visit(InternalRequestMessage message) {
        message.accept(manager);
    }

    @Override
    public void visit(InternalReceivedMessage message) {
        if (message instanceof InternalReceivedFileRelated fileRelated) {
            fileRelated.accept((FileMessageVisitor) fileManager);
        } else {
            message.accept(fileManager);
        }
    }
}
