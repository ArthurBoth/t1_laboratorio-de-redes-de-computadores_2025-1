package io;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import constants.Constants;
import constants.Exceptions.FileSearchException;
import io.consoleIO.TerminalManager;
import io.fileIO.FileManager;
import network.messages.IONetworkMessage;
import network.messages.NetworkIOMessage;
import network.messages.TerminalIOMessage;
import network.threads.NetworkNode;

public class IOManager implements Runnable {
    private BlockingQueue<IONetworkMessage> networkSenderQueue;    
    private BlockingQueue<NetworkIOMessage> networkReceiverQueue;  

    private BlockingQueue<TerminalIOMessage> consoleReceiverQueue;  
    private ConcurrentHashMap<NetworkNode, Integer> activeNodes;

    private TerminalManager terminal;
    private FileManager fileManager;
    
    private volatile boolean running;

    public IOManager(BlockingQueue<IONetworkMessage> networkSenderQueue,
                     BlockingQueue<NetworkIOMessage> networkReceiverQueue,
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
        NetworkIOMessage networkMessage;
        int listening = 2;
        running = true;

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
        switch (message.getType()) {
            case EXIT         -> {processExit();}                
            case SEND_MESSAGE -> {processSendMessage(message);}
            case SEND_FILE    -> {processSendFile(message);}
        }
    }

    private void processMessage(NetworkIOMessage message) {
        switch (message.getType()) {
            case TALK -> {processTalk(message);}
            case FILE -> {processReceivedFile(message);}
        }
    }

    private void processExit() {
        Thread terminalThread = terminal.stopConsole();
        terminalThread.interrupt();
        networkSenderQueue.offer(IONetworkMessage.exit());

        running = false;
    }

    private void processSendMessage(TerminalIOMessage message) {
        networkSenderQueue.offer(
            IONetworkMessage.talk(message.getStringField())
        );
    }

    private void processSendFile(TerminalIOMessage message) {
        File file;
        String fileName;
        long fileSize;
        Queue<byte[]> fullData;
        byte[] chunkData;
        String fileHash;

        try {
            fileName = message.getStringField();
            file     = fileManager.getFile(Constants.Configs.Paths.INPUT_FOLDER_PATH + fileName);
            fileSize = file.length();
            fullData = fileManager.getFileData(file);
            fileHash = fileManager.getFileHash(file);

            networkSenderQueue.offer(
                IONetworkMessage.file(fileName).fileSize(fileSize)
            );
            for (int i = 0; i < fullData.size(); i++) {
                chunkData = fullData.poll();
                networkSenderQueue.offer(
                    IONetworkMessage.chunk(i).chunkData(chunkData)
                );
            }
            networkSenderQueue.offer(
                IONetworkMessage.end(fileHash)
            );
        } catch (FileSearchException e) {
            terminal.errorMessage(e.getMessage());
        }
    }

    private void processTalk(NetworkIOMessage message) {
        // TODO
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    private void processReceivedFile(NetworkIOMessage message) {
        // TODO
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
