package io.console;

import static utils.Constants.Strings.IP_PORT_FORMAT;

import java.net.InetAddress;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import messages.ThreadMessage;
import messages.internal.InternalMessage;
import network.NetworkNode;
import utils.ConsoleLogger;
import utils.Constants;

public class TerminalManager implements Runnable {
    private BlockingQueue<InternalMessage> messageSenderQueue;
    private ConcurrentLinkedQueue<String> errorMessages;

    private volatile boolean running;
    private ConcurrentHashMap<InetAddress, NetworkNode> activeNodes; // ip -> node

    // used for sending files
    private volatile boolean waitingManager;
    private volatile int sendPercentage;

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
        running = true;

        ConsoleLogger.logWhite("Starting console...");
        while (running) {
            printErrors();
            printMenu();
            processInput();
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
        ConsoleLogger.logCyan("Menu:");
        ConsoleLogger.logYellow("Exit");
        ConsoleLogger.logYellow("Devices");
        ConsoleLogger.logYellow("Talk");
        ConsoleLogger.logYellow("sendfile");
        ConsoleLogger.logYellow(">> ", false);
    }

    private void processInput() {
        String input;
        String command;

        input   = scanner.nextLine();
        input   = input.trim();
        command = input.split(" ",3)[0];

        if (input.isEmpty()) {
            ConsoleLogger.logRed("Invalid command. Please try again.");
            return;
        }
        if (command.equalsIgnoreCase("exit")) {
            exit();
            sleep();
            return;
        }
        if (command.equalsIgnoreCase("devices")) {
            processDevices();
            return;
        }
        if (command.equalsIgnoreCase("talk")) {
            processTalk(input);
            return;
        }
        if (command.equalsIgnoreCase("sendfile")) {
            processSend(input);
            return;
        }
    }

    private void sleep() {
        try {
            Thread.sleep(Constants.Configs.THREAD_TIMEOUT_MS);
        } catch (InterruptedException e) {
            return;
        }
    }

    public void errorMessage(String message) {
        errorMessages.add(message);
        waitingManager = false;
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

    private void exit() {
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

    /**
     * Validates a command and returns a node if it exists.
     * @param args The command arguments, node should be {@code args[1]}
     * @return The node if it exists, null otherwise.
     */
    private NetworkNode validateCommand(String[] args) {
        NetworkNode node;
        if (args.length < 3) {
            ConsoleLogger.logRed("Invalid command. Please try again.");
            return null;
        }

        try {
            node = activeNodes.get(InetAddress.getByName(args[1]));
        } catch (Exception e) {
            node = null;
        }

        if (node == null) {
            ConsoleLogger.logRed("Node not found. Aborting...");
        }

        return node;
    }

    private void processTalk(String input) {
        String[]    args    = input.split(" ");
        NetworkNode node    = validateCommand(args);
        String      message = args[2];

        if (node == null) return;

        messageSenderQueue.offer(
            ThreadMessage.internalMessage(getClass())
                .request()
                .send()
                .talk(message)
                .to(node.getAddress())
                .at(node.getPort())
        );
    }

    private void processSend(String input) {
        String[]    args     = input.split(" ");
        NetworkNode node     = validateCommand(args);
        String      fileName = args[2];

        if (node == null) return;
        messageSenderQueue.offer(
            ThreadMessage.internalMessage(getClass())
                .request()
                .send()
                .file(fileName)
                .to(node.getAddress())
                .at(node.getPort())
        );

        waitManagerFileAck(); // blocks the console until the file is sent
    }

    public void updateFileProgress(int progress) {
        sendPercentage = progress;
        waitingManager = false;
    }

    private void waitManagerFileAck() {
        int prevoiusErrorCount = errorMessages.size();
            waitingManager     = true;

        while (waitingManager) {
            try {
                Thread.sleep(Constants.Configs.THREAD_TIMEOUT_MS);
            } catch (InterruptedException e) {
                return;
            }
        }

        if (errorMessages.size() > prevoiusErrorCount) {
            return;
        }
        displayLoadingBar();
    }

    private void displayLoadingBar() {
        String loadingBarFormat;
        String bar;

        sendPercentage = 0;
        loadingBarFormat = "\rLoading: [%s] %d%%";

        while(sendPercentage < 100) {
            while (waitingManager) {} // wait for updates
            waitingManager = true;

            bar = "=".repeat(sendPercentage) + " ".repeat(100 - sendPercentage);
            ConsoleLogger.logWhite(loadingBarFormat.formatted(bar, sendPercentage), false);
        }

        ConsoleLogger.logGreen("\nFile sent successfully!");
    }
}
