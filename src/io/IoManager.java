package io;

import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import interfaces.visitors.FileMessageVisitor;
import interfaces.visitors.internal.InternalRequestMessageVisitor;
import io.console.TerminalManager;
import io.files.FileManager;
import messages.ThreadMessage;
import messages.internal.InternalMessage;
import messages.internal.received.InternalReceivedMessage;
import messages.internal.requested.InternalRequestAbortFileSendingMessage;
import messages.internal.requested.InternalRequestDisplayFailureMessage;
import messages.internal.requested.InternalRequestDisplaySuccessMessage;
import messages.internal.requested.InternalRequestExitMessage;
import messages.internal.requested.InternalRequestResendMessage;
import messages.internal.requested.InternalRequestUpdateSendStatusMessage;
import messages.internal.requested.send.InternalRequestSendAckMessage;
import messages.internal.requested.send.InternalRequestSendChunkMessage;
import messages.internal.requested.send.InternalRequestSendEndMessage;
import messages.internal.requested.send.InternalRequestSendFileMessage;
import messages.internal.requested.send.InternalRequestSendFullFileMessage;
import messages.internal.requested.send.InternalRequestSendNAckMessage;
import messages.internal.requested.send.InternalRequestSendTalkMessage;
import network.NetworkNode;
import utils.Exceptions.FileException;

public class IoManager implements Runnable, InternalRequestMessageVisitor {
    private BlockingQueue<InternalMessage> networkSenderQueue;
    private BlockingQueue<InternalMessage> networkReceiverQueue;

    private BlockingQueue<InternalMessage> ioReceiverQueue;
    private ConcurrentHashMap<InetAddress, NetworkNode> activeNodes;  // ip -> node

    private TerminalManager terminal;
    private FileManager fileManager;
    
    private volatile boolean running;

    public IoManager(BlockingQueue<InternalMessage> networkSenderQueue,
                     BlockingQueue<InternalMessage> networkReceiverQueue,
                     ConcurrentHashMap<InetAddress, NetworkNode> activeNodes) {
        this.networkSenderQueue   = networkSenderQueue;
        this.networkReceiverQueue = networkReceiverQueue;
        this.activeNodes          = activeNodes;

        ioReceiverQueue = new LinkedBlockingQueue<InternalMessage>();
        fileManager     = new FileManager(ioReceiverQueue);
    }

    @Override
    public void run() {
        IoHandler handler;
        InternalMessage message;

        running = true;
        handler = new IoHandler(this, fileManager);

        startConsole();
        while (running) {
            message = listen();
            message.accept(handler);
        }
    }

    private InternalMessage listen() {
        InternalMessage message;
        while (true) {
            message = networkReceiverQueue.poll();
            if (message != null) return message;
            message = ioReceiverQueue.poll();
            if (message != null) return message;
        }
    }

    private void startConsole() {
        terminal = new TerminalManager(ioReceiverQueue, activeNodes);
        new Thread(() -> terminal.run()).start();
    }

    private void processExit() {
        Thread terminalThread = terminal.stopConsole();
        terminalThread.interrupt();
        networkSenderQueue.offer(
            ThreadMessage.internalMessage(getClass())
                .request()
                .exit()
            );

        running = false;
    }

    // ****************************************************************************************************
    // Implementation of InternalRequestMessageVisitor

    @Override
    public void visit(InternalRequestExitMessage message) {
        message.accept(fileManager);
        processExit();
    }

    // **************************************************
    // Log and forward the message

    @Override
    public void visit(InternalRequestSendTalkMessage message) {
        message.accept(fileManager);
        networkSenderQueue.offer(message);
    }

    @Override
    public void visit(InternalRequestSendFileMessage message) {
        try {
            message.accept((FileMessageVisitor) fileManager);
        } catch (FileException e) {
            terminal.errorMessage(e.getMessage());
            return;
        }
        terminal.setBytesToSend(message.getFileSize());
        networkSenderQueue.offer(message);
    }

    @Override
    public void visit(InternalRequestSendAckMessage message) {
        message.accept(fileManager);
        networkSenderQueue.offer(message);
    }

    @Override
    public void visit(InternalRequestSendNAckMessage message) {
        message.accept(fileManager);
        networkSenderQueue.offer(message);
    }

    public void visit(InternalReceivedMessage message) {
        message.accept(fileManager);
        networkSenderQueue.offer(message);
    }

    @Override
    public void visit(InternalRequestSendChunkMessage message) {
        message.accept(fileManager);
        networkSenderQueue.offer(message);
    }

    @Override
    public void visit(InternalRequestSendEndMessage message) {
        message.accept(fileManager);
        networkSenderQueue.offer(message);
    }

    // **************************************************
    // Log and update the progress

    public void visit(InternalRequestSendFullFileMessage message) {
        int sentBytes = 0;
        message.accept((FileMessageVisitor) fileManager);
        terminal.updateFileProgress(sentBytes);
    }

    @Override
    public void visit(InternalRequestUpdateSendStatusMessage message) {
        message.accept(fileManager);
        terminal.updateFileProgress(message.getSize());
    }

    @Override
    public void visit(InternalRequestDisplaySuccessMessage message) {
        message.accept(fileManager);
        terminal.setEndMessage(message.getMessage());
    }

    @Override
    public void visit(InternalRequestDisplayFailureMessage message) {
        message.accept(fileManager);
        terminal.setEndMessage(message.getMessage());
    }

    @Override
    public void visit(InternalRequestAbortFileSendingMessage message) {
        message.accept(fileManager);
        terminal.errorMessage(message.getMessage());
    }

    // **************************************************
    // Log only

    @Override
    public void visit(InternalRequestResendMessage message) {
        message.accept(fileManager);
    }
}
