package io;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import constants.Constants;
import io.consoleIO.TerminalManager;
import io.fileIO.FileLogger;
import network.threads.NetworkNode;
import network.threads.messages.ExternalMessage;
import network.threads.messages.ThreadMessage;

public class IOManager implements Runnable{
    private BlockingQueue<ThreadMessage> networkQueue;  // only-send
    private BlockingQueue<ThreadMessage> receiver;      // only-receive
    private ConcurrentHashMap<NetworkNode, Integer> activeNodes;



    private TerminalManager terminal;
    private FileLogger logger;
    
    private volatile boolean running;

    public IOManager(BlockingQueue<ThreadMessage> networkQueue,
                     ConcurrentHashMap<NetworkNode, Integer> activeNodes) {
        this.networkQueue = networkQueue;
        receiver          = new LinkedBlockingQueue<ThreadMessage>();
        logger            = new FileLogger();
        this.activeNodes  = activeNodes;
    }

    @Override
    public void run() {
        ThreadMessage message;
        running = true;

        startConsole();
        while (running) {
            try {
                message = receiver.poll(Constants.Configs.THREAD_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                if (message != null) processMessage(message);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void startConsole() {
        terminal = new TerminalManager(receiver, activeNodes);
        new Thread(() -> terminal.run()).start();
    }

    private void processMessage(ThreadMessage message) {
        switch (message.getType()) {
            // Internal Messages
            case EXIT      -> {processExit();}
            case SEND_FILE -> {processSendFile();}
            
            // External Messages
            case TALK -> {processTalk((ExternalMessage) message);}

            // Default case
            default -> {throw new IllegalArgumentException("Invalid message type: " + message.getType());}
        }
    }

    private void processExit() {
        Thread terminalThread = terminal.stopConsole();
        terminalThread.interrupt();
        networkQueue.offer(ThreadMessage.internalMessage().exit());

        running = false;
    }

    private void processTalk(ExternalMessage talkMessage) {
        if (talkMessage.getMessageBytes().length > Constants.Configs.MAX_MESSAGE_SIZE) {
            terminal.errorMessage("""
                    Message is too long. 
                    Maximum message size is %d bytes.
                    Your message has %d bytes""".formatted(
                            Constants.Configs.MAX_MESSAGE_SIZE,
                            talkMessage.getMessageBytes().length));
            return;
        }

        networkQueue.offer(talkMessage);
        logger.logSent(talkMessage.getMessage());
    }

    private void processSendFile() {
        // TODO
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
