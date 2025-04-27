package io;

import java.io.File;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import interfaces.visitors.internal.InternalRequestMessageVisitor;
import io.console.TerminalManager;
import io.files.FileManager;
import messages.ThreadMessage;
import messages.internal.InternalMessage;
import messages.internal.received.InternalReceivedMessage;
import messages.internal.requested.InternalRequestExitMessage;
import messages.internal.requested.InternalRequestResendMessage;
import messages.internal.requested.send.InternalRequestSendAckMessage;
import messages.internal.requested.send.InternalRequestSendFileMessage;
import messages.internal.requested.send.InternalRequestSendNAckMessage;
import messages.internal.requested.send.InternalRequestSendTalkMessage;
import network.NetworkNode;
import utils.Constants;
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
        IoHandler handler = new IoHandler(this);
        InternalMessage message;
        int listening; 
        
        listening = 2;
        running   = true;

        startConsole();
        while (running) {
            try {
                message = networkReceiverQueue.poll(
                    (Constants.Configs.THREAD_TIMEOUT_MS / listening), 
                    TimeUnit.MILLISECONDS
                    );
                if (message != null) message.accept(handler);
                message = ioReceiverQueue.poll(
                    (Constants.Configs.THREAD_TIMEOUT_MS / listening), 
                    TimeUnit.MILLISECONDS
                    );
                if (message != null) message.accept(handler);
            } catch (InterruptedException e) {
                return;
            }
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

    private void processSendFile(InternalRequestSendFileMessage message) {
        File file;
        String fileName;
        byte[] fullData;
        String fileHash;

        try {
            fileName = message.getFileName();
            file     = fileManager.getFile(Constants.Configs.Paths.SEND_FOLDER_PATH + fileName);
            fullData = fileManager.getFileData(file);
            fileHash = fileManager.getFileHash(file);

            networkSenderQueue.offer(
                message.fileData(fullData)
                    .fileHash(fileHash)
            );
        } catch (FileException e) {
            terminal.errorMessage(e.getMessage());
        }
    }

    // ****************************************************************************************************
    // Implementation of InternalRequestMessageVisitor

    @Override
    public void visit(InternalRequestExitMessage message) {
        message.accept(fileManager); // logs the message
        processExit();
    }

    @Override
    public void visit(InternalRequestSendTalkMessage message) {
        message.accept(fileManager);        // logs the message
        networkSenderQueue.offer(message);  // forwards the message
    }

    @Override
    public void visit(InternalRequestSendFileMessage message) {
        message.accept(fileManager); // logs the message
        processSendFile(message);
    }

    @Override
    public void visit(InternalRequestSendAckMessage message) {
        message.accept(fileManager);        // logs the message
        networkSenderQueue.offer(message);  // forwards the message
    }

    @Override
    public void visit(InternalRequestSendNAckMessage message) {
        message.accept(fileManager);        // logs the message
        networkSenderQueue.offer(message);  // forwards the message
    }

    public void visit(InternalReceivedMessage message) {
        message.accept(fileManager);        // logs the message
        networkSenderQueue.offer(message);  // forwards the message
    }

    @Override
    public void visit(InternalRequestResendMessage message) {
        message.accept(fileManager);        // logs the message
        networkSenderQueue.offer(message);  // forwards the message
    }
}
