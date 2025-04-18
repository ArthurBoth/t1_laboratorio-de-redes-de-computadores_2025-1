package io;

import java.io.File;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import constants.Constants;
import constants.Exceptions.FileSearchException;
import io.consoleIO.TerminalManager;
import io.fileIO.FileManager;
import io.fileIO.filePartition.FileData;
import network.messages.foreign.ForeignMessage;
import network.messages.foreign.ForeignResponseWrapper;
import network.messages.internal.IONetworkMessage;
import network.messages.internal.InternalMessage;
import network.messages.internal.TerminalIOMessage;
import network.threads.NetworkNode;

public class IOManager implements Runnable {
    private BlockingQueue<IONetworkMessage> networkSenderQueue;    
    private BlockingQueue<ForeignMessage> networkReceiverQueue;  

    private BlockingQueue<TerminalIOMessage> consoleReceiverQueue;  
    private ConcurrentHashMap<NetworkNode, Integer> activeNodes; // node -> last received Id

    private TerminalManager terminal;
    private FileManager fileManager;
    private HashMap<String, FileData> fileDataMap;
    
    private volatile boolean running;

    public IOManager(BlockingQueue<IONetworkMessage> networkSenderQueue,
                     BlockingQueue<ForeignMessage> networkReceiverQueue,
                     ConcurrentHashMap<NetworkNode, Integer> activeNodes) {
        this.networkSenderQueue   = networkSenderQueue;
        this.networkReceiverQueue = networkReceiverQueue;
        this.activeNodes          = activeNodes;

        consoleReceiverQueue = new LinkedBlockingQueue<TerminalIOMessage>();
        fileManager          = new FileManager();
    }

    @Override
    public void run() {
        TerminalIOMessage consoleMessage;
        ForeignMessage networkMessage;
        int listening; 
        
        listening = 2;
        running   = true;

        startConsole();
        while (running) {
            try {
                networkMessage = networkReceiverQueue.poll(
                    (Constants.Configs.THREAD_TIMEOUT_MS / listening), 
                    TimeUnit.MILLISECONDS
                    );
                if (networkMessage != null) processMessage(networkMessage);
                consoleMessage = consoleReceiverQueue.poll(
                    (Constants.Configs.THREAD_TIMEOUT_MS / listening), 
                    TimeUnit.MILLISECONDS
                    );
                if (consoleMessage != null) processMessage(consoleMessage);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void startConsole() {
        terminal = new TerminalManager(consoleReceiverQueue, activeNodes);
        new Thread(() -> terminal.run()).start();
    }

    private void processMessage(TerminalIOMessage message) {
        fileManager.log(message);
        switch (message.getType()) {
            case EXIT         -> {processExit();}                
            case SEND_MESSAGE -> {processSendMessage(message);}
            case SEND_FILE    -> {processSendFile(message);}
        }
    }

    private void processMessage(ForeignMessage message) {
        ForeignResponseWrapper response;
        IONetworkMessage networkMessage;

        response = message.accept(fileManager);
        if (response == null) return; // no response needed

        if (response.ackResponse()) {
            networkMessage = InternalMessage.ioToNetwork()
                .sendAck(response.getSourceIp())
                .ackId(response.getMessageId());
        } else {
            networkMessage = InternalMessage.ioToNetwork()
                .sendNAck(response.getSourceIp())
                .nAckId(response.getMessageId())
                .string(response.getMessage());          
        }

        networkSenderQueue.offer(networkMessage);
    }

    private void processExit() {
        Thread terminalThread = terminal.stopConsole();
        terminalThread.interrupt();
        networkSenderQueue.offer(InternalMessage.ioToNetwork().exit());

        running = false;
    }

    private void processSendMessage(TerminalIOMessage message) {
        networkSenderQueue.offer(
            InternalMessage.ioToNetwork()
                .sendTalk(message.getDestinationIp())
                .string(message.getStringField())
        );
    }

    private void processSendFile(TerminalIOMessage message) {
        File file;
        String fileName;
        long fileSize;
        Queue<byte[]> fullData;
        String fileHash;

        try {
            fileName = message.getStringField();
            file     = fileManager.getFile(Constants.Configs.Paths.SEND_FOLDER_PATH + fileName);
            fileSize = file.length();
            fullData = fileManager.getFileData(file);
            fileHash = fileManager.getFileHash(file);

            fileDataMap.put(fileHash, new FileData(fullData, fileHash));

            networkSenderQueue.offer(
                InternalMessage.ioToNetwork()
                    .sendFile(message.getDestinationIp())
                    .fileName(fileName)
                    .fileSize(fileSize)
            );
        } catch (FileSearchException e) {
            terminal.errorMessage(e.getMessage());
        }
    }
}
