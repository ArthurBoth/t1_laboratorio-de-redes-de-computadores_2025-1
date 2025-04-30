package network.threads;

import static utils.Constants.Configs.PRINT_LOGS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

import messages.internal.InternalMessage;
import utils.ConsoleLogger;
import utils.Constants;

public class ReceiverThread extends NetworkThread {
    private BlockingQueue<InternalMessage> messageQueue;
    private MessageEncoder encoder;
    
    protected ReceiverThread(DatagramSocket socket, BlockingQueue<InternalMessage> messageQueue) {
        super(socket);
        this.messageQueue = messageQueue;
        this.encoder      = new MessageEncoder();
    }
    @Override
    public void run() {
        DatagramPacket packet;
        InternalMessage receivedMessage;
        
        if (PRINT_LOGS) ConsoleLogger.logPurple("ReceiverThread started.");
        while (running) {
            try {
                packet          = waitForPacket();
                receivedMessage = encoder.decodePacket(packet);
                if (PRINT_LOGS) ConsoleLogger.logPurple(receivedMessage.getMessage());
                messageQueue.put(receivedMessage);
            } catch (SocketException e) {
                ConsoleLogger.logRed("Socket has been closed. Stopping ReceiverThread.");
                return;
            } catch (IOException e) {
                ConsoleLogger.logError("Error while receiving message", e);
                ConsoleLogger.logRed("Discarding packet.");
                return;
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private DatagramPacket waitForPacket() throws IOException, InterruptedException {
        byte[]         buffer = new byte[Constants.Configs.MAX_MESSAGE_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        socket.receive(packet);
        return packet;
    }
}
