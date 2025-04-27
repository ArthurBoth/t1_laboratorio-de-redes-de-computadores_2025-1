package io.files;

import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import interfaces.visitors.FileMessageVisitor;
import interfaces.visitors.LoggerVisitor;
import io.files.filePartition.*;
import messages.ThreadMessage;
import messages.internal.InternalMessage;
import messages.internal.received.*;
import messages.internal.requested.send.InternalRequestSendFileMessage;
import messages.internal.requested.send.InternalRequestSendFullFileMessage;
import messages.internal.requested.send.InternalRequestSendMessage;
import messages.internal.requested.send.InternalRequestSendTalkMessage;
import utils.ConsoleLogger;
import utils.Constants;
import utils.Exceptions.FileException;

public class FileManager implements FileMessageVisitor, LoggerVisitor {
    private FileLogger logger;
    private HashMap<InetAddress, FileAssembler> receivingFiles;   // ip -> File
    private HashMap<InetAddress, FileDisassembler> sendingfiles;  // ip -> File
    private BlockingQueue<InternalMessage> managerSenderQueue;

    public FileManager(BlockingQueue<InternalMessage> managerSenderQueue) {
        this.managerSenderQueue = managerSenderQueue;

        logger         = new FileLogger();
        receivingFiles = new HashMap<InetAddress, FileAssembler>();
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
            managerSenderQueue.offer(
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
        
        if (receivingFiles.containsKey(message.getSourceIp())) {
            managerSenderQueue.offer(
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
            managerSenderQueue.offer(
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

        receivingFiles.put(message.getSourceIp(), fileAssembler);
        managerSenderQueue.offer(
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
        fileAssembler  = receivingFiles.get(sourceIp);

        if (fileAssembler == null) {
            managerSenderQueue.offer(
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
            managerSenderQueue.offer(
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

        managerSenderQueue.offer(
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
        fileAssembler = receivingFiles.get(sourceIp);

        if (fileAssembler == null) {
            managerSenderQueue.offer(
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

            managerSenderQueue.offer(
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

        receivingFiles.remove(sourceIp);
        
        if (fileAssembler.getErrorMessage() == null) {
            managerSenderQueue.offer(
                ThreadMessage.internalMessage(getClass())
                    .request()
                    .send()
                    .ack(message.getMessageId())
                    .to(sourceIp)
                    .at(message.getPort())
            );
        } else {
            managerSenderQueue.offer(
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

    @Override
    public void visit(InternalRequestSendFileMessage message) {
        FileDisassembler fileDisassembler;

        logger.logSent(message);
        try {
            fileDisassembler = FileDisassembler.of(message.getFileName());
        } catch (NoSuchAlgorithmException e) {
            fileDisassembler = null;
        }

        if (fileDisassembler == null) {
            throw new FileException("No such algorithm");
        }

        message.setFileSize(fileDisassembler.getFileSize());
        sendingfiles.put(message.getDestinationIp(), fileDisassembler);
    }


    @Override
    public void visit(InternalRequestSendFullFileMessage message) {
        FileDisassembler fileDisassembler;
        byte[] chunkData;
        int sequenceNumber;
        InetAddress destinationIp;
        int port;

        logger.logInternal(message);
        fileDisassembler = sendingfiles.get(message.getDestinationIp());

        if (fileDisassembler == null) return;

        destinationIp  = message.getDestinationIp();
        port           = message.getPort();
        sequenceNumber = 0;
        while((chunkData = fileDisassembler.readChunk()) != null) {
            managerSenderQueue.offer(
                ThreadMessage.internalMessage(getClass())
                    .request()
                    .send()
                    .chunk(sequenceNumber++)
                    .data(chunkData)
                    .to(destinationIp)
                    .at(port)
            );
        }

        managerSenderQueue.offer(
            ThreadMessage.internalMessage(getClass())
                .request()
                .send()
                .end(fileDisassembler.computeHash())
                .to(destinationIp)
                .at(port)
        );
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
