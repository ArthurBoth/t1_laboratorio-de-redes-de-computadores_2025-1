package io;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import constants.ConfigConstants;
import io.consoleIO.TerminalManager;
import io.fileIO.FileLogger;
import network.threads.messages.ThreadMessage;

public class IOManager implements Runnable{
    private BlockingQueue<ThreadMessage> networkQueue;  // only-send
    private BlockingQueue<ThreadMessage> receiver;      // only-receive

    private TerminalManager terminal;
    private FileLogger logger;
    
    private volatile boolean running;

    public IOManager(BlockingQueue<ThreadMessage> networkQueue) {
        this.networkQueue = networkQueue;
        receiver         = new LinkedBlockingQueue<ThreadMessage>();
        logger            = new FileLogger();
    }

    @Override
    public void run() {
        ThreadMessage message;
        running = true;

        startConsole();
        while (running) {
            try {
                message = receiver.poll(ConfigConstants.THREAD_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                if (message != null) processMessage(message);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void startConsole() {
        terminal = new TerminalManager(receiver);
        new Thread(() -> terminal.run()).start();
    }

    private void processMessage(ThreadMessage message) {
        switch (message.getType()) {
            // Internal Messages
            case EXIT      -> {processExit();}
            case SEND_FILE -> {processSendFile();}
            
            // External Messages
            case TALK -> {processTalk();}

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

    private void processSendFile() {
        // TODO
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    private void processTalk() {
        // TODO
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
