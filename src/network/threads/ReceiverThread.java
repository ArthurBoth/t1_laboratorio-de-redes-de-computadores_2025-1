package network.threads;

import static utils.Constants.Configs.PRINT_LOGS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

import io.consoleIO.ConsoleLogger;
import network.messages.ThreadMessage;
import network.messages.internal.InternalMessage;
import utils.Constants;

public class ReceiverThread extends NetworkThread {
    private BlockingQueue<ThreadMessage> messageQueue;
    
    protected ReceiverThread(DatagramSocket socket, BlockingQueue<ThreadMessage> messageQueue) {
        super(socket);
        this.messageQueue = messageQueue;
    }
    @Override
    public void run() {
        DatagramPacket packet;
        InternalMessage receivedMessage;
        
        if (PRINT_LOGS) ConsoleLogger.logPurple("ReceiverThread started.");
        while (running) {
            try {
                packet          = waitForPacket();
                receivedMessage = decodePacket(packet);
                if (receivedMessage != null) messageQueue.put(receivedMessage);
            } catch (IOException e) {
                ConsoleLogger.logError("Error while receiving message", e);
                ConsoleLogger.logRed("Discarding packet.");
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private DatagramPacket waitForPacket() throws IOException, InterruptedException {
        byte[]           data = new byte[Constants.Configs.MAX_MESSAGE_SIZE];
        DatagramPacket packet = new DatagramPacket(data, data.length);

        socket.receive(packet);
        return packet;
    }

    private InternalMessage decodePacket(DatagramPacket packet) {
        return ThreadMessage.decodeMessage(packet.getAddress(), packet.getData());
    }
}
