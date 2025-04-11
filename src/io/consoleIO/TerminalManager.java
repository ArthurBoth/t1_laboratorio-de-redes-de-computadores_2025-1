package io.consoleIO;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import constants.ConfigConstants;
import network.threads.messages.ThreadMessage;

public class TerminalManager implements Runnable{
    private BlockingQueue<ThreadMessage> messageQueue;  // only-send
    private Scanner scanner;
    private LinkedList<String> errorMessages;

    private volatile boolean running;

    public TerminalManager(BlockingQueue<ThreadMessage> messageQueue) {
        this.messageQueue = messageQueue;
        scanner           = new Scanner(System.in);
        errorMessages     = new LinkedList<String>();
    }

    @Override
    public void run() {
        int userResponse;
        running = true;

        ConsoleLogger.logWhite("Starting console...");
        while (running) {
            printErrors();
            printMenu();
            userResponse = getUserInputChoice();
            processResponse(userResponse);
        }
        stopConsole();
    }

    private void printErrors() {
        if (errorMessages.isEmpty()) return;
        ConsoleLogger.logRed("Errors:");
        for (String error : errorMessages) {
            ConsoleLogger.logWhite(error);
        }
        errorMessages.clear();
    }

    private void printMenu() {
        ConsoleLogger.logCyan("Menu:");
        ConsoleLogger.logYellow("[0] Exit");
        ConsoleLogger.logYellow("[1] Send a message");
        ConsoleLogger.logYellow("[2] Send a file");
        ConsoleLogger.logYellow(">> ", false);
    }

    private int getUserInputChoice() {
        int response = -1;
        try {
            response = Integer.parseInt(scanner.nextLine());
            if (response == - Integer.MAX_VALUE)
                return -1; // Protect against killing the scanner
            return response;
        } catch (NumberFormatException e) {
            ConsoleLogger.logRed("Invalid input. Please enter a number.");
        } catch (IllegalStateException e) {
            return - Integer.MAX_VALUE; // Scanner is closed
        }
        return -1;
    }

    public void errorMessage(String message) {
        ConsoleLogger.logRed(message);
        printMenu();
    }

    private void processResponse(int input) {
        boolean wait = true;

        switch (input) {
            case 0 -> {
                exit(false); 
                wait = false;
            }
            case 1 -> {processTalk();}
            case 2 -> {processSend();}
            case (- Integer.MAX_VALUE) -> {exit(true);}
            default -> ConsoleLogger.logRed("Invalid choice. Please try again.");
        }

        try {
            if (wait) Thread.sleep(ConfigConstants.THREAD_TIMEOUT_MS);
        } catch (InterruptedException e) {
            return;
        }
    }

    private void exit(boolean systemExit) {
        if (systemExit) 
            ConsoleLogger.logRed("Scanner is closed. ", false);
        ConsoleLogger.logWhite("Exiting console...");
        running = false;
        messageQueue.add(ThreadMessage.internalMessage().exit());
    }

    public Thread stopConsole() {
        running = false;
        scanner.close();
        return Thread.currentThread();
    }

    private void processTalk(){
        // TODO
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    private void processSend() {
        // TODO
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
