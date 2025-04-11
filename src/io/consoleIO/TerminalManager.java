package io.consoleIO;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import network.threads.ThreadMessage;

public class TerminalManager implements Runnable{
    private BlockingQueue<ThreadMessage> networkQueue;
    private boolean running;
    private final Scanner scanner;

    public TerminalManager(BlockingQueue<ThreadMessage> networkQueue) {
        scanner           = new Scanner(System.in);
        this.networkQueue = networkQueue;

        running = true;
    }

    @Override
    public void run() {
        int userResponse;
        ConsoleLogger.logWhite("Starting console...");
        while (running) {
            printMenu();
            userResponse = getUserInputChoice();
            processResponse(userResponse);
        }

        try {
            Thread.currentThread().interrupt();
        } finally {
            scanner.close();
        }
    }
    
    private void printMenu() {
        ConsoleLogger.logCyan("Menu:");
        ConsoleLogger.logWhite("[0] Exit");
        ConsoleLogger.logWhite("[1] TALK");
        ConsoleLogger.logWhite(">> ", false);
    }

    private int getUserInputChoice() {
        int response = -1;
        while (running) {
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
        }
        return -1;
    }

    private void processResponse(int input) {
        switch (input) {
            case 0 -> {aaaaaaaaaaaaaaaaaaaaa(false);}
            case 1 -> {processTalk();}
            case (- Integer.MAX_VALUE) -> {aaaaaaaaaaaaaaaaaaaaa(true);}
            default -> ConsoleLogger.logRed("Invalid choice. Please try again.");
        }
    }

    public void aaaaaaaaaaaaaaaaaaaaa(boolean systemExit) {
        if (systemExit) 
            ConsoleLogger.logRed("Scanner is closed. ", false);
        ConsoleLogger.logWhite("Exiting console...");
        running = false;
        networkQueue.add(ThreadMessage.internalMessage(true).message("0"));
    }

    private void processTalk(){
        // TODO
    }
}
