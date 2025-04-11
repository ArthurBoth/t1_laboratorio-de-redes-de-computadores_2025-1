package io;

import java.util.concurrent.BlockingQueue;

import io.consoleIO.TerminalManager;
import io.fileIO.FileLogger;
import network.threads.ThreadMessage;

public class IOManager {
    BlockingQueue<ThreadMessage> networkQueue;
    TerminalManager terminal;
    FileLogger logger;

    public IOManager(BlockingQueue<ThreadMessage> networkQueue) {
        this.networkQueue = networkQueue;
        logger = new FileLogger();
    }

    public void startConsole() {
        terminal = new TerminalManager(networkQueue);
        new Thread(() -> terminal.run()).start();
    }

    public void endExecution() {
        terminal.stopConsole();
    }
}
