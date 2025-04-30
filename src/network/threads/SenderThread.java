package network.threads;

import static utils.Constants.Configs.PRINT_LOGS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import messages.foreign.ForeignMessage;
import utils.ConsoleLogger;
import utils.Constants;
import utils.FileUtils;

public class SenderThread extends NetworkThread {
    private BlockingQueue<ForeignMessage> messageQueue;
    private MessageEncoder encoder;

    public SenderThread(DatagramSocket socket, BlockingQueue<ForeignMessage> messageQueue) {
        super(socket);
        this.messageQueue = messageQueue;
        this.encoder      = new MessageEncoder();
    }

    @Override
    public void run() {
        ForeignMessage message;

        if (PRINT_LOGS) ConsoleLogger.logBlue("SenderThread started.");
        while (running) {
            try {
                message = messageQueue.poll(Constants.Configs.SOCKET_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                if (message != null) sendMessage(message);
            } catch (InterruptedException e) {
                super.running = false;  
                return;
            }
        }
    }
    
    private void sendMessage(ForeignMessage message) {
        InetAddress address;
        int port;
        byte[] data;
        DatagramPacket packet;

        try {
            data    = message.encode(encoder);
            port    = message.getPort();
            address = message.getDestinationIp();
            packet  = new DatagramPacket(data, data.length, address, port);

            socket.send(packet);
            if (PRINT_LOGS) ConsoleLogger.logBlue("Sent message to %s:%d".formatted(address.getHostName(), port));
            if (PRINT_LOGS) ConsoleLogger.logBlue(FileUtils.byteArrayToString(data));
            if (PRINT_LOGS) ConsoleLogger.logBlue("(%d bytes)".formatted(packet.getLength()));
        } catch (IOException e) {
            ConsoleLogger.logError("Failed to send message", e);   
        }
    } 
}
