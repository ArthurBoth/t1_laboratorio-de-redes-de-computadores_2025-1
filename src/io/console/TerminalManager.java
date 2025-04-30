package io.console;

import static utils.Constants.Strings.IP_PORT_FORMAT;

import java.net.InetAddress;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import messages.ThreadMessage;
import messages.internal.InternalMessage;
import network.NetworkNode;
import utils.ConsoleLogger;
import utils.Constants;
import utils.Constants.Configs;

public class TerminalManager implements Runnable {
    private BlockingQueue<InternalMessage> messageSenderQueue;
    private ConcurrentLinkedQueue<String> errorMessages;

    private volatile boolean running;
    private ConcurrentHashMap<InetAddress, NetworkNode> activeNodes; // ip -> node

    // used for sending files
    private BlockingQueue<Boolean> waitingManager;
    private volatile long bytesToSend;
    private volatile long sentBytes;
    private volatile String endMessage;

    private Scanner scanner;

    public TerminalManager(BlockingQueue<InternalMessage> messageSenderQueue,
            ConcurrentHashMap<InetAddress, NetworkNode> activeNodes) {
        this.messageSenderQueue = messageSenderQueue;
        this.activeNodes        = activeNodes;

        scanner        = new Scanner(System.in);
        errorMessages  = new ConcurrentLinkedQueue<String>();
        waitingManager = new LinkedBlockingQueue<Boolean>();
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
        String[] args;
        String input;
        String command;

        input   = scanner.nextLine();
        input   = input.trim();
        args    = input.split(" ", 3);
        command = args[0];

        if (command.equalsIgnoreCase("exit")) {
            exit();
            sleep();
            return;
        }
        if (command.equalsIgnoreCase("devices")) {
            processDevices();
            return;
        }
        if (command.equalsIgnoreCase("talk") && args.length == 3) {
            processTalk(args);
            return;
        }
        if (command.equalsIgnoreCase("sendfile") && args.length == 3) {
            processSend(args);
            return;
        }

        ConsoleLogger.logRed("Invalid command. Please try again.");
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
        waitingManager.add(false);
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
            ConsoleLogger.logWhite("Node %d:%n\tName: ".formatted(i), false);
            ConsoleLogger.logYellow(node.getName());
            ConsoleLogger.logWhite("\tAddress: ", false);
            ConsoleLogger.logYellow(
                IP_PORT_FORMAT.formatted("\t" + node.getAddress().getHostAddress(), node.getPort())
            );
            ConsoleLogger.logWhite(
                "\t%d seconds since last message".formatted(node.getSecondsSinceHeartbeatMessage())
            );
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
        String[] ipSplit;
        
        if (args.length < 3) {
            ConsoleLogger.logRed("Invalid command. Please try again.");
            return null;
        }

        try {
            node = activeNodes.get(InetAddress.getByName(args[1]));
        } catch (Exception e) {
            node = null;
        }
        try {
            if ((node == null) && (Configs.ALLOW_CUSTOM_IPS)) {
                ipSplit = args[1].split(":");

                node = NetworkNode.of(
                    InetAddress.getByName(ipSplit[0]), Integer.parseInt(ipSplit[1])
                );
            }
        } catch (Exception e) {
            node = null;
        }

        if (node == null) {
            ConsoleLogger.logRed("Node not found. Aborting...");
        }

        return node;
    }

    private void processTalk(String[] args) {
        NetworkNode node;
        String message;

        node = validateCommand(args);
        if (node == null) return;
        
        message = args[2];
        messageSenderQueue.offer(
            ThreadMessage.internalMessage(getClass())
                .request()
                .send()
                .talk(message)
                .to(node.getAddress())
                .at(node.getPort())
        );
    }

    private void processSend(String[] args) {
        NetworkNode node;
        String fileName;
        
        node = validateCommand(args);
        if (node == null) return;
        
        fileName = args[2];
        
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

    public void updateFileProgress(long chunkSize) {
        sentBytes      += chunkSize;
        waitingManager.add(false);
    }

    public void setBytesToSend(long bytesToSend) {
        this.bytesToSend = bytesToSend;
    }

    public void setEndMessage(String endMessage) {
        this.endMessage = endMessage;
        waitingManager.add(false);
    }

    private void waitManagerFileAck() {
        int prevoiusErrorCount = errorMessages.size();

        try {
            waitingManager.take();
        } catch (InterruptedException e) {
            return;
        }

        if (errorMessages.size() > prevoiusErrorCount) {
            return;
        }

        try {
            displayLoadingBar();
        } catch (InterruptedException e) {
            return;
        }
    }

    private void displayLoadingBar() throws InterruptedException {
        String loadingBarFormat;
        String bar;
        int sendPercentage;

        sentBytes        = 0;
        sendPercentage   = 0;
        loadingBarFormat = "\rLoading: [%s] %d%%";

        while(sendPercentage < 100) {
            waitingManager.take(); // wait for updates

            sendPercentage = (int) (sentBytes * 100 / bytesToSend);

            bar = "=".repeat(sendPercentage) + " ".repeat(100 - sendPercentage);
            ConsoleLogger.logWhite(loadingBarFormat.formatted(bar, sendPercentage), false);
        }

        waitingManager.take(); // wait for ack to END

        ConsoleLogger.logWhite("\n" + endMessage);
    }
}
