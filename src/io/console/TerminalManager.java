package io.console;

import static utils.Constants.Strings.IP_PORT_FORMAT;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import messages.ThreadMessage;
import messages.internal.InternalMessage;
import network.NetworkNode;
import utils.ConsoleLogger;
import utils.Constants;
import utils.FileUtils;
import utils.Constants.Configs;

public class TerminalManager implements Runnable {
    private BlockingQueue<InternalMessage> messageSenderQueue;
    private ConcurrentLinkedQueue<String> errorMessages;

    private volatile boolean running;
    private ConcurrentHashMap<InetAddress, NetworkNode> activeNodes; // ip -> node

    private Scanner scanner;

    public TerminalManager(BlockingQueue<InternalMessage> messageSenderQueue,
            ConcurrentHashMap<InetAddress, NetworkNode> activeNodes) {
        this.messageSenderQueue = messageSenderQueue;
        scanner = new Scanner(System.in);
        errorMessages = new ConcurrentLinkedQueue<String>();
        this.activeNodes = activeNodes;
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
        if (errorMessages.isEmpty())
            return;

        ConsoleLogger.logRed("Errors:");
        while (!errorMessages.isEmpty()) {
            String error = errorMessages.poll();
            if (error == null)
                break;
            ConsoleLogger.logWhite(error);
        }
    }

    private void printMenu() {
        // TODO list all nodes on the network (Via a 'devices' command)
        ConsoleLogger.logCyan("Menu:");
        ConsoleLogger.logYellow("[0] Exit");
        ConsoleLogger.logYellow("[1] Devices");
        ConsoleLogger.logYellow("[2] Talk");      // TODO change to 'talk <ip> <message>' format
        ConsoleLogger.logYellow("[3] sendfile");  // TODO change to 'sendfile <ip> <filename>' format
        ConsoleLogger.logYellow(">> ", false);
    }

    private int getUserInputChoice() {
        int response = -1;
        try {
            response = Integer.parseInt(scanner.nextLine());
            if (response == -Integer.MAX_VALUE)
                return -1; // Protect against killing the scanner
            return response;
        } catch (NumberFormatException e) {
            ConsoleLogger.logRed("Invalid input. Please enter a number.");
        } catch (IllegalStateException e) {
            return -Integer.MAX_VALUE; // Scanner is closed
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
            case 1 -> { processDevices(); }
            case 2 -> { processTalk(); }
            case 3 -> { processSend(); }
            case (-Integer.MAX_VALUE) -> { exit(true); }
            default -> ConsoleLogger.logRed("Invalid choice. Please try again.");
        }

        try {
            if (wait)
                Thread.sleep(Constants.Configs.THREAD_TIMEOUT_MS);
        } catch (InterruptedException e) {
            return;
        }
    }

    private void processDevices() {
        NetworkNode[] nodes = activeNodes.values().stream().toArray(NetworkNode[]::new);
        printActiveNodes(nodes);
    }

    private void printActiveNodes(NetworkNode[] nodes) {
        NetworkNode node;
        ConsoleLogger.logCyan("Active nodes:");
        for (int i = 0; i < nodes.length; i++) {
            node = nodes[i];
            ConsoleLogger.logWhite("Node %d:%n\tName: ".formatted(i));
            ConsoleLogger.logYellow(node.getName());
            ConsoleLogger.logWhite("\tAddress: ", false);
            ConsoleLogger.logYellow(
                IP_PORT_FORMAT.formatted("\t" + node.getAddress().getHostAddress(), node.getPort())
            );
            ConsoleLogger.logWhite("\t%d seconds since last message", false);
        }
    }

    private void exit(boolean systemExit) {
        if (systemExit)
            ConsoleLogger.logRed("Scanner is closed. ", false);
        ConsoleLogger.logWhite("Exiting console...");
        running = false;
        messageSenderQueue.offer(
                ThreadMessage.internalMessage(this.getClass())
                        .request()
                        .exit());
    }

    public Thread stopConsole() {
        running = false;
        scanner.close();
        return Thread.currentThread();
    }

    private void processTalk() {
        String[] nodes;
        String node;
        String message;

        nodes = activeNodes.keySet().stream()
                .map(InetAddress::getHostAddress)
                .toArray(String[]::new);

        node = getNodeToSend(nodes);
        if (node == null)
            return;

        ConsoleLogger.logYellow("Enter the message to send: ", false);
        message = scanner.nextLine();
        if (message.isEmpty()) {
            ConsoleLogger.logRed("Invalid message. Aborting...");
            return;
        }

        ConsoleLogger.logWhite("Sending message to node...");

        try {
            messageSenderQueue.offer(
                    ThreadMessage.internalMessage(this.getClass())
                            .request()
                            .send()
                            .talk(message)
                            .to(node));
        } catch (UnknownHostException e) {
            ConsoleLogger.logError("Unable to send, message discarted:", e);
        }
    }

    private void processSend() {
        String[] nodes;
        String node;
        String fileName;

        nodes = activeNodes.keySet().stream()
                .map(InetAddress::getHostAddress)
                .toArray(String[]::new);

        node = getNodeToSend(nodes);
        if (node == null)
            return;

        ConsoleLogger.logYellow("Enter the File's name (with extension) to send: ", false);
        fileName = scanner.nextLine();
        if (!FileUtils.isValidFileName(fileName)) {
            ConsoleLogger.logRed("Invalid file name. Aborting...");
            return;
        }

        try {
            messageSenderQueue.offer(
                    ThreadMessage.internalMessage(this.getClass())
                            .request()
                            .send()
                            .file(fileName)
                            .to(node));
        } catch (UnknownHostException e) {
            ConsoleLogger.logError("Unable to send, message discarted:", e);
        }
    }
}
