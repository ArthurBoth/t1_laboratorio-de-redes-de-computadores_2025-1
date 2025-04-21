package io;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import interfaces.visitors.InternalSentMessageVisitor;
import io.consoleIO.TerminalManager;
import io.fileIO.FileManager;
import messages.ThreadMessage;
import messages.foreign.ForeignMessage;
import messages.foreign.ForeignTalkMessage;
import messages.internal.InternalMessage;
import messages.internal.sentMessages.InternalExitMessage;
import messages.internal.sentMessages.InternalSentAckMessage;
import messages.internal.sentMessages.InternalSentFileMessage;
import messages.internal.sentMessages.InternalSentNAckMessage;
import messages.internal.sentMessages.InternalSentTalkMessage;
import network.threads.NetworkNode;
import utils.Constants;
import utils.Exceptions.FileSearchException;

public class IOManager implements Runnable, InternalSentMessageVisitor {
    private BlockingQueue<ThreadMessage> networkSenderQueue;    
    private BlockingQueue<ThreadMessage> networkReceiverQueue;  

    private BlockingQueue<InternalMessage> ioReceiverQueue;
    private ConcurrentHashMap<NetworkNode, Integer> activeNodes; // node -> seconds since last message

    private TerminalManager terminal;
    private FileManager fileManager;
    
    private volatile boolean running;

    public IOManager(BlockingQueue<ThreadMessage> networkSenderQueue,
                     BlockingQueue<ThreadMessage> networkReceiverQueue,
                     ConcurrentHashMap<NetworkNode, Integer> activeNodes) {
        this.networkSenderQueue   = networkSenderQueue;
        this.networkReceiverQueue = networkReceiverQueue;
        this.activeNodes          = activeNodes;

        ioReceiverQueue = new LinkedBlockingQueue<InternalMessage>();
        fileManager     = new FileManager(ioReceiverQueue);
    }

    @Override
    public void run() {
        ThreadMessage message;
        InternalMessage internalMessage;
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
                if (message != null) message.accept(this);
                internalMessage = ioReceiverQueue.poll(
                    (Constants.Configs.THREAD_TIMEOUT_MS / listening), 
                    TimeUnit.MILLISECONDS
                    );
                if (internalMessage != null) internalMessage.accept(this);
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
                .sendMessage()
                .exit()
            );

        running = false;
    }

    private void processSendTalk(InternalSentTalkMessage message) {
        networkSenderQueue.offer(message);
    }

    private void processSendFile(InternalSentFileMessage message) {
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
        } catch (FileSearchException e) {
            terminal.errorMessage(e.getMessage());
        }
    }

    private void processSendAck(InternalSentAckMessage message) {
        networkSenderQueue.offer(
            ThreadMessage.foreignMessage(getClass())
                .ack(message.getAcknowledgedMessageId())
                .to(message.getDestinationIp())
            );
    }

    private void processSendNAck(InternalSentNAckMessage message) {
        networkSenderQueue.offer(
            ThreadMessage.foreignMessage(getClass())
                .nAck(message.getNonAcknowledgedMessageId())
                .because(message.getReason())
                .to(message.getDestinationIp())
            );
    }

    // **************************************************************************************************************
    // Visitor pattern for IOManager

    @Override
    public void visit(InternalExitMessage message) {
        message.accept(fileManager); // logs the message
        processExit();
    }

    @Override
    public void visit(InternalSentTalkMessage message) {
        message.accept(fileManager); // logs the message
        processSendTalk(message);
    }

    @Override
    public void visit(InternalSentFileMessage message) {
        message.accept(fileManager); // logs the message
        processSendFile(message);
    }

    @Override
    public void visit(InternalSentAckMessage message) {
        message.accept(fileManager); // logs the message
        processSendAck(message);
    }

    @Override
    public void visit(InternalSentNAckMessage message) {
        message.accept(fileManager); // logs the message
        processSendNAck(message);
    }

    // *******************************************************
    // Logging messages

    @Override
    public void visit(ForeignMessage message) {
        message.accept(fileManager);
    }

    // Can still visiting ForeignTalkMessage
    public void visit(ForeignTalkMessage message) {
        message.accept(fileManager);
    }

    @Override
    public void visit(InternalMessage message) {
        message.accept(fileManager);
    }
}
