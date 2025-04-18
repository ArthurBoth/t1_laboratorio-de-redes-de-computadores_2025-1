package io.consoleIO;

import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import constants.Constants;
import io.fileIO.FileUitls;
import network.messages.internal.InternalMessage;
import network.messages.internal.TerminalIOMessage;
import network.threads.NetworkNode;

public class TerminalManager implements Runnable{
    private BlockingQueue<TerminalIOMessage> messageSenderQueue;
    private ConcurrentLinkedQueue<String> errorMessages;
    
    private volatile boolean running;
    private ConcurrentHashMap<NetworkNode, Integer> activeNodes;

    private Scanner scanner;

    public TerminalManager(BlockingQueue<TerminalIOMessage> messageSenderQueue, 
                            ConcurrentHashMap<NetworkNode, Integer> activeNodes) {
        this.messageSenderQueue = messageSenderQueue;
        scanner                 = new Scanner(System.in);
        errorMessages           = new ConcurrentLinkedQueue<String>();
        this.activeNodes        = activeNodes;
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
        while (!errorMessages.isEmpty()) {
            String error = errorMessages.poll();
            if (error == null) break;
            ConsoleLogger.logWhite(error);
        }
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
        errorMessages.add(message);
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
            if (wait) Thread.sleep(Constants.Configs.THREAD_TIMEOUT_MS);
        } catch (InterruptedException e) {
            return;
        }
    }

    private void exit(boolean systemExit) {
        if (systemExit) ConsoleLogger.logRed("Scanner is closed. ", false);
        ConsoleLogger.logWhite("Exiting console...");
        running = false;
        messageSenderQueue.add(InternalMessage.terminalToIO().exit());
    }

    public Thread stopConsole() {
        running = false;
        scanner.close();
        return Thread.currentThread();
    }

    private int getNodeToSend(String[] nodes) {
        int nodeNumber;


        printActiveNodes(nodes);
        ConsoleLogger.logYellow("Enter the node number to send to: ", false);
        nodeNumber = getUserInputChoice();
        
        if (nodeNumber >= 0 || nodeNumber < activeNodes.size()) {
            ConsoleLogger.logRed("Invalid node number. Aborting...");
            return -1;
        }

        return nodeNumber;
    }

    private void processTalk() {
        String[] nodes;
        int nodeNumber;
        String message;
        
        nodes = activeNodes.keySet().stream()
                .map(NetworkNode::getIpAddress)
                .toArray(String[]::new);

        nodeNumber = getNodeToSend(nodes);
        if (nodeNumber < 0) return;

        ConsoleLogger.logYellow("Enter the message to send: ", false);
        message = scanner.nextLine();
        if (message.isEmpty()) {
            ConsoleLogger.logRed("Invalid message. Aborting...");
            return;
        }

        ConsoleLogger.logWhite("Sending message to node...");

        try {
            messageSenderQueue.add(
                InternalMessage.terminalToIO()
                    .sendMessage(message)
                    .toIp(nodes[nodeNumber])
            );
        } catch (UnknownHostException e) {
            ConsoleLogger.logError("Unable to send, message discarted:", e);
        }
    }

    private void printActiveNodes(String[] nodes) {
        ConsoleLogger.logCyan("Active nodes:");
        for (int i = 0; i < nodes.length; i++) {
            ConsoleLogger.logWhite(String.format("[%d] %s", i, nodes[i]));
        }
    }

    private void processSend() {
        String[] nodes;
        int nodeNumber;
        String fileName;

        nodes = activeNodes.keySet().stream()
                .map(NetworkNode::getIpAddress)
                .toArray(String[]::new);

        nodeNumber = getNodeToSend(nodes);
        if (nodeNumber < 0) return;

        ConsoleLogger.logYellow("Enter the File's name (with extension) to send: ", false);
        fileName = scanner.nextLine();
        if (FileUitls.isValidFileName(fileName)) {
            ConsoleLogger.logRed("Invalid file name. Aborting...");
            return;
        }

        try {
            messageSenderQueue.add(
                InternalMessage.terminalToIO()
                    .sendFile(fileName)
                    .toIp(nodes[nodeNumber])
            );
        } catch (UnknownHostException e) {
            ConsoleLogger.logError("Unable to send, message discarted:", e);
        }
    }
}
