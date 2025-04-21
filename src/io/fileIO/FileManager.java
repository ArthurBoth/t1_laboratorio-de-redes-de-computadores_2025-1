package io.fileIO;

import static utils.Constants.Configs.Paths.RECEIVE_FOLDER_PATH;

import java.io.File;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import interfaces.visitors.FileMessageVisitor;
import interfaces.visitors.LoggerVisitor;
import io.fileIO.filePartition.FileChunk;
import messages.ThreadMessage;
import messages.foreign.ForeignMessage;
import messages.foreign.ForeignTalkMessage;
import messages.internal.InternalMessage;
import messages.internal.receivedMessages.InternalReceivedChunkMessage;
import messages.internal.receivedMessages.InternalReceivedEndMessage;
import messages.internal.receivedMessages.InternalReceivedFileMessage;
import utils.Constants;
import utils.FileUtils;
import utils.Exceptions.FileSearchException;

public class FileManager implements FileMessageVisitor, LoggerVisitor {
    private FileLogger logger;
    private HashMap<InetAddress, HashMap<Integer, FileChunk>> fileDataMap;  // ip -> (seq -> data)
    private HashMap<InetAddress, File> fileMap;                             // ip -> File
    private BlockingQueue<InternalMessage> messageSenderQueue;

    public FileManager(BlockingQueue<InternalMessage> messageSenderQueue) {
        this.messageSenderQueue = messageSenderQueue;
        logger                  = new FileLogger();
        fileDataMap             = new HashMap<InetAddress, HashMap<Integer, FileChunk>>();
        fileMap                 = new HashMap<InetAddress, File>();
    }

    public File getFile(String fileName) throws FileSearchException {
        File file = new File(fileName);

        if (!file.exists())
            throw new FileSearchException("File not found");
        if (file.isDirectory())
            throw new FileSearchException("File is a directory");
        if (!file.canRead())
            throw new FileSearchException("File is not readable");
        if (file.length() > Constants.Configs.MAX_FILE_SIZE)
            throw new FileSearchException("File is too large");
        if (file.length() == 0)
            throw new FileSearchException("File is empty");

        return file;
    }

    public byte[] getFileData(File file) throws FileSearchException {
        return FileIO.readFile(file.toPath());
    }

    public String getFileHash(File file) throws FileSearchException {
        try {
            return FileIO.hashFile(file.toPath());
        } catch (NoSuchAlgorithmException e) {
            throw new FileSearchException("Error hashing file", e);
        }
    }

    private void log(ForeignMessage message) {
        logger.logForeign(message);
    }

    private void log(InternalMessage message) {
        logger.logInternal(message);
    }

    private void log(ForeignTalkMessage message) {
        logger.logTalk(message);
    }
    
    // ****************************************************************************************************************
    // Visitor pattern for InternalMessage

    // ********************************************************
    // File management

    @Override
    public void visit(InternalReceivedFileMessage message) {
        String errorMessage;
        String fileName;
        long fileSize;
        File file;
        
        log(message);

        errorMessage = null;
        fileName     = message.getFileName();
        fileSize     = message.getFileSize();
        file         = new File(RECEIVE_FOLDER_PATH + fileName);

        errorMessage = FileUtils.problemsCreatingFile(file);

        if (fileSize > Constants.Configs.MAX_FILE_SIZE) 
            errorMessage = "File is too large";
        if (fileSize <= 0) 
            errorMessage = "File is empty";
        if (fileDataMap.containsKey(message.getSourceIp())) 
            errorMessage = "Already receiving a file from that ip";
        
        if (errorMessage != null) {
            messageSenderQueue.offer(
                ThreadMessage.internalMessage(getClass())
                    .sendMessage()
                    .nAck(message.getMessageId())
                    .because(errorMessage)
                    .to(message.getSourceIp())
            );
            return;
        }
        
        fileDataMap.put(message.getSourceIp(), new HashMap<Integer, FileChunk>());
        fileMap.put(message.getSourceIp(), file);
        messageSenderQueue.offer(
            ThreadMessage.internalMessage(getClass())
                .sendMessage()
                .ack(message.getMessageId())
                .to(message.getSourceIp())
        );
    }

    @Override
    public void visit(InternalReceivedChunkMessage message) {
        String errorMessage;
        int sequenceNumber;
        byte[] chunkData;
        InetAddress sourceIp;
        FileChunk fileChunk;

        log(message);

        errorMessage   = null;
        sequenceNumber = message.getSequenceNumber();
        chunkData      = message.getData();
        sourceIp       = message.getSourceIp();
        fileChunk      = new FileChunk(chunkData, sequenceNumber);

        if (fileDataMap.get(sourceIp) == null) {
            errorMessage = "Not receiving a file from that ip";
        } else if (fileDataMap.get(sourceIp).putIfAbsent(sequenceNumber, fileChunk) != null) {
            logger.logInternal(
                Constants.Strings.DISCARTED_CHUNK_FORMAT.formatted(sequenceNumber, chunkData.length)
            );
        }

        if (errorMessage != null) {
            messageSenderQueue.offer(
                ThreadMessage.internalMessage(getClass())
                    .sendMessage()
                    .nAck(message.getMessageId())
                    .because(errorMessage)
                    .to(message.getSourceIp())
            );
            return;
        }
        messageSenderQueue.offer(
            ThreadMessage.internalMessage(getClass())
                .sendMessage()
                .ack(message.getMessageId())
                .to(message.getSourceIp())
        );
    }

    @Override
    public void visit(InternalReceivedEndMessage message) {
        String errorMessage;
        String receivedHash;
        String fileHash;
        InetAddress sourceIp;
        byte[] fileData;
        File file;

        log(message);

        errorMessage = null;
        receivedHash = message.getFileHash();
        sourceIp     = message.getSourceIp();
        file         = fileMap.get(sourceIp);

        if (fileDataMap.get(sourceIp) == null)
            errorMessage = "Not receiving a file from that ip";
        
        fileData = assembleFile(sourceIp);
        if (fileData == null) 
            errorMessage = "Missing chunks";

        try {
            fileHash = FileUtils.getFileHash(fileData);
        } catch (NoSuchAlgorithmException e) {
            fileHash = null;
        }

        if (fileHash == null) 
            errorMessage = "Error hashing file";
        if (!fileHash.equals(receivedHash))
            errorMessage = "File hash does not match";

        fileMap.remove(sourceIp);
        fileDataMap.remove(sourceIp);
        
        if (errorMessage != null) {
            messageSenderQueue.offer(
                ThreadMessage.internalMessage(getClass())
                    .sendMessage()
                    .nAck(message.getMessageId())
                    .because(errorMessage)
                    .to(sourceIp)
            );
            return;
        }
        FileIO.writeFile(file.getName(), fileData);
        messageSenderQueue.offer(
            ThreadMessage.internalMessage(getClass())
                .sendMessage()
                .ack(message.getMessageId())
                .to(sourceIp)
        );
    }

    private byte[] assembleFile(InetAddress sourceIp) {
        List<FileChunk> chunkList;
        FileChunk chunk;
        int index;
        byte[] fileData;

        chunkList = fileDataMap.get(sourceIp)
                    .values().stream().toList();
        chunkList.sort(null);
        for (int i = 0; i < chunkList.size(); i++) {
            if (chunkList.get(i).getChunkSeqNumber() != i) {
                return null; // Missing chunks
            }
        }

        fileData = new byte[chunkList.stream().mapToInt(FileChunk::getChunkSize).sum()];
        index    = 0;
        for (int i = 0; i < chunkList.size(); i++) {
            chunk = chunkList.get(i);
            System.arraycopy(
                chunk.getChunkData(), 
                0, 
                fileData, 
                index, 
                chunk.getChunkSize()
            );
            index += chunk.getChunkSize();
        }
        return fileData;
    }

    // ********************************************************
    // log only

    @Override
    public void visit(ForeignMessage message) {
        log(message);
    }

    @Override
    public void visit(ForeignTalkMessage message) {
        log(message);
    }

    @Override
    public void visit(InternalMessage message) {
        log(message);
    }
}
