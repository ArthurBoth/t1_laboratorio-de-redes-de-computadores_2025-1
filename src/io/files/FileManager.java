package io.files;

import java.io.File;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import interfaces.visitors.FileMessageVisitor;
import interfaces.visitors.LoggerVisitor;
import io.files.filePartition.FileAssembler;
import messages.ThreadMessage;
import messages.internal.InternalMessage;
import messages.internal.received.InternalReceivedChunkMessage;
import messages.internal.received.InternalReceivedEndMessage;
import messages.internal.received.InternalReceivedFileMessage;
import messages.internal.received.InternalReceivedMessage;
import messages.internal.received.InternalReceivedTalkMessage;
import messages.internal.requested.send.InternalRequestSendMessage;
import messages.internal.requested.send.InternalRequestSendTalkMessage;
import utils.ConsoleLogger;
import utils.Constants;
import utils.Exceptions.FileException;

public class FileManager implements FileMessageVisitor, LoggerVisitor {
    private FileLogger logger;
    private HashMap<InetAddress, FileAssembler> fileMap;  // ip -> File
    private BlockingQueue<InternalMessage> messageSenderQueue;

    public FileManager(BlockingQueue<InternalMessage> messageSenderQueue) {
        this.messageSenderQueue = messageSenderQueue;

        logger  = new FileLogger();
        fileMap = new HashMap<InetAddress, FileAssembler>();
    }

    public File getFile(String fileName) throws FileException {
        File file = new File(fileName);

        if (!file.exists())
            throw new FileException("File not found");
        if (file.isDirectory())
            throw new FileException("File is a directory");
        if (!file.canRead())
            throw new FileException("File is not readable");
        if (file.length() > Constants.Configs.MAX_FILE_SIZE)
            throw new FileException("File is too large");
        if (file.length() == 0)
            throw new FileException("File is empty");

        return file;
    }
    
    // ****************************************************************************************************
    // Visitor pattern for InternalMessage

    // **************************************************
    // File management

    @Override
    public void visit(InternalReceivedFileMessage message) {
        String fileName;
        long fileSize;
        FileAssembler fileAssembler;
        
        logger.logReceived(message);

        fileName = message.getFileName();
        fileSize = message.getFileSize();

        try {
            fileAssembler = FileAssembler.of(fileName, fileSize);
        } catch (NoSuchAlgorithmException e) {
            ConsoleLogger.logError(e);
            messageSenderQueue.offer(
                ThreadMessage.internalMessage(getClass())
                    .request()
                    .send()
                    .nAck(message.getMessageId())
                    .because("Error creating file assembler")
                    .to(message.getSourceIp())
                    .at(message.getPort())
            );
            return;
        }
        
        if (fileMap.containsKey(message.getSourceIp())) {
            messageSenderQueue.offer(
                ThreadMessage.internalMessage(getClass())
                    .request()
                    .send()
                    .nAck(message.getMessageId())
                    .because("Already receiving a file from that ip")
                    .to(message.getSourceIp())
                    .at(message.getPort())
            );
            return;
        }

        if (fileAssembler == null) {
            messageSenderQueue.offer(
                ThreadMessage.internalMessage(getClass())
                    .request()
                    .send()
                    .nAck(message.getMessageId())
                    .because("Invalid FileName or FileSize")
                    .to(message.getSourceIp())
                    .at(message.getPort())
            );
            return;
        }

        fileMap.put(message.getSourceIp(), fileAssembler);
        messageSenderQueue.offer(
            ThreadMessage.internalMessage(getClass())
                .request()
                .send()
                .ack(message.getMessageId())
                .to(message.getSourceIp())
                .at(message.getPort())
        );
    }

    @Override
    public void visit(InternalReceivedChunkMessage message) {
        int sequenceNumber;
        byte[] chunkData;
        InetAddress sourceIp;
        FileAssembler fileAssembler;

        logger.logReceived(message);

        sequenceNumber = message.getSequenceNumber();
        chunkData      = message.getData();
        sourceIp       = message.getSourceIp();
        fileAssembler  = fileMap.get(sourceIp);

        if (fileAssembler == null) {
            messageSenderQueue.offer(
                ThreadMessage.internalMessage(getClass())
                    .request()
                    .send()
                    .nAck(message.getMessageId())
                    .because("Not receiving a file from that ip")
                    .to(message.getSourceIp())
                    .at(message.getPort())
            );
            return;
        }
        if (!fileAssembler.addPacket(sequenceNumber, chunkData)) {
            if (fileAssembler.getErrorMessage() == null)
                // Out of order packet, wait for timeout to resend
                return;

            logger.logInternal(
                Constants.Strings.DISCARTED_CHUNK_FORMAT.formatted(
                    sequenceNumber, chunkData.length, fileAssembler.getErrorMessage()
                )
            );
            messageSenderQueue.offer(
                ThreadMessage.internalMessage(getClass())
                    .request()
                    .send()
                    .nAck(message.getMessageId())
                    .because(fileAssembler.getErrorMessage())
                    .to(message.getSourceIp())
                    .at(message.getPort())
            );
            return;
        }

        messageSenderQueue.offer(
            ThreadMessage.internalMessage(getClass())
                .request()
                .send()
                .ack(message.getMessageId())
                .to(message.getSourceIp())
                .at(message.getPort())
        );
    }

    @Override
    public void visit(InternalReceivedEndMessage message) {
        String receivedHash;
        InetAddress sourceIp;
        FileAssembler fileAssembler;

        logger.logReceived(message);

        receivedHash  = message.getFileHash();
        sourceIp      = message.getSourceIp();
        fileAssembler = fileMap.get(sourceIp);

        if (fileAssembler == null) {
            messageSenderQueue.offer(
                ThreadMessage.internalMessage(getClass())
                    .request()
                    .send()
                    .nAck(message.getMessageId())
                    .because("Not receiving a file from that ip")
                    .to(message.getSourceIp())
                    .at(message.getPort())
            );
            return;
        }
        
        if (!fileAssembler.completeCreation(receivedHash)) {
            if (fileAssembler.getErrorMessage() == null)
                // Out of order packet, wait for timeout to resend
                return;

            messageSenderQueue.offer(
                ThreadMessage.internalMessage(getClass())
                    .request()
                    .send()
                    .nAck(message.getMessageId())
                    .because(fileAssembler.getErrorMessage())
                    .to(message.getSourceIp())
                    .at(message.getPort())
            );
        }

        if (!fileAssembler.isComplete()) return;

        fileMap.remove(sourceIp);
        
        if (fileAssembler.getErrorMessage() == null) {
            messageSenderQueue.offer(
                ThreadMessage.internalMessage(getClass())
                    .request()
                    .send()
                    .ack(message.getMessageId())
                    .to(sourceIp)
                    .at(message.getPort())
            );
        } else {
            messageSenderQueue.offer(
                ThreadMessage.internalMessage(getClass())
                    .request()
                    .send()
                    .nAck(message.getMessageId())
                    .because(fileAssembler.getErrorMessage())
                    .to(message.getSourceIp())
                    .at(message.getPort())
            );
        }
    }

    // **************************************************
    // log only

    @Override
    public void visit(InternalMessage message) {
        logger.logInternal(message);
    }

    @Override
    public void visit(InternalReceivedMessage message) {
        logger.logReceived(message);
    }

    @Override
    public void visit(InternalReceivedTalkMessage message) {
        logger.logReceivedTalk(message);
    }

    @Override
    public void visit(InternalRequestSendMessage message) {
        logger.logSent(message);
    }

    @Override
    public void visit(InternalRequestSendTalkMessage message) {
        logger.logSentTalk(message);
    }
}
