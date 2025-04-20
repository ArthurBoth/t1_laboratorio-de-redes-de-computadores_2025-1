package network.threads;

import static utils.Constants.Configs.PRINT_LOGS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import io.consoleIO.ConsoleLogger;
import network.messages.ExternalMessage;
import network.messages.ThreadMessage;
import utils.Constants;

public class SenderThread extends NetworkThread {
    public BlockingQueue<ThreadMessage> messageQueue;

    public SenderThread(DatagramSocket socket, BlockingQueue<ThreadMessage> messageQueue) {
        super(socket);
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        ExternalMessage message;

        if (PRINT_LOGS) ConsoleLogger.logBlue("SenderThread started.");
        while (running) {
            try {
                message = (ExternalMessage) messageQueue.poll(Constants.Configs.SOCKET_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                if (message != null) sendMessage(message);
            } catch (InterruptedException e) {
                super.running = false;  
                return;
            }
        }
    }
    
    private void sendMessage(ExternalMessage message) {
        InetAddress address;
        int port;
        byte[] data;
        DatagramPacket packet;

        try {
            data    = message.getMessageBytes();
            port    = socket.getPort();
            address = message.getDestinationIp();
            packet  = new DatagramPacket(data, data.length, address, port);

            socket.send(packet);

            if (PRINT_LOGS) {
                ConsoleLogger.logBlue("Sent message: %s to %s:%d".formatted(
                    message.getMessage(), address.toString(), port
                ));
            }
        } catch (IOException e) {
            ConsoleLogger.logError("Failed to send message", e);   
        }
    } 
}
