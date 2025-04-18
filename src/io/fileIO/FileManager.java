package io.fileIO;

import java.io.File;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import constants.Constants;
import constants.Exceptions.FileSearchException;
import interfaces.visitors.ForeignMessageVisitor;
import io.fileIO.filePartition.FileChunk;
import network.messages.foreign.ForeignAck;
import network.messages.foreign.ForeignChunk;
import network.messages.foreign.ForeignEnd;
import network.messages.foreign.ForeignFile;
import network.messages.foreign.ForeignHeartbeat;
import network.messages.foreign.ForeignMessage;
import network.messages.foreign.ForeignNAck;
import network.messages.foreign.ForeignResponseWrapper;
import network.messages.foreign.ForeignTalk;
import network.messages.internal.TerminalIOMessage;

import static constants.Constants.Configs.Paths.RECEIVE_FOLDER_PATH;

public class FileManager implements ForeignMessageVisitor {
    private FileLogger logger;
    private HashMap<InetAddress, HashMap<Integer, FileChunk>> fileDataMap; // ip -> (seq -> data)
    private HashMap<InetAddress, File> fileMap; // ip -> File

    public FileManager() {
        logger      = new FileLogger();
        fileDataMap = new HashMap<InetAddress, HashMap<Integer, FileChunk>>();
        fileMap = new HashMap<InetAddress, File>();
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

    public Queue<byte[]> getFileData(File file) throws FileSearchException {
        int chunkSize;
        int lastChunkSize;
        int numberOfChunks;
        Queue<byte[]> splitFileData;
        byte[] fileData;
        byte[] chunkData;

        chunkSize      = (Constants.Configs.MAX_MESSAGE_SIZE - Constants.Configs.MIN_CHUNK_SIZE);
        lastChunkSize  = (int) (file.length() % chunkSize);
        numberOfChunks = (int) (file.length() / chunkSize) + (lastChunkSize > 0 ? 1 : 0);
        splitFileData  = new LinkedList<byte[]>();
        fileData       = FileIO.readFile(file.toPath());

        if (fileData == null) throw new FileSearchException("File is empty");

        for (int i = 0; i < numberOfChunks; i++) {
            if (i == numberOfChunks - 1) {
                chunkData = new byte[lastChunkSize];
                System.arraycopy(fileData, i * chunkSize, chunkData, 0, lastChunkSize);
            } else {
                chunkData = new byte[chunkSize];
                System.arraycopy(fileData, i * chunkSize, chunkData, 0, chunkSize);
            }
            splitFileData.add(chunkData);
        }

        return splitFileData;
    }

    public String getFileHash(File file) throws FileSearchException {
        try {
            return FileIO.hashFile(file.toPath());
        } catch (NoSuchAlgorithmException e) {
            throw new FileSearchException("Error hashing file", e);
        }
    }

    public void log(TerminalIOMessage message) {
        logger.logInternal(message);
    }

    public void log(ForeignMessage message) {
        logger.logForeign(message);
    }

    public void log(ForeignTalk message) {
        logger.logTalk(message);
    }

    // ****************************************************************************************************************
    // Visitor pattern for ForeignMessage

    // ********************************************************
    // File management

    @Override
    public ForeignResponseWrapper visit(ForeignFile visitable) {
        String errorMessage;
        String fileName;
        long fileSize;
        File file;
        
        log(visitable);

        errorMessage = null;
        fileName     = visitable.getFileName();
        fileSize     = visitable.getFileSize();
        file         = new File(RECEIVE_FOLDER_PATH + fileName);

        errorMessage = FileUitls.problemsCreatingFile(file);

        if (fileSize > Constants.Configs.MAX_FILE_SIZE) 
            errorMessage = "File is too large";
        if (fileSize <= 0) 
            errorMessage = "File is empty";
        if (fileDataMap.containsKey(visitable.getSourceIp())) 
            errorMessage = "Already receiving a file from that ip";

        if (errorMessage != null) {
            return ForeignResponseWrapper
                        .notAck(visitable.getMessageId())
                        .because(errorMessage)
                        .from(visitable.getSourceIp());
        }

        fileDataMap.put(visitable.getSourceIp(), new HashMap<Integer, FileChunk>());
        fileMap.put(visitable.getSourceIp(), file);
        return ForeignResponseWrapper.ack(visitable.getMessageId()).from(visitable.getSourceIp());
    }

    @Override
    public ForeignResponseWrapper visit(ForeignChunk visitable) {
        String errorMessage;
        int sequenceNumber;
        byte[] chunkData;
        InetAddress sourceIp;
        FileChunk fileChunk;

        log(visitable);

        errorMessage   = null;
        sequenceNumber = visitable.getSequenceNumber();
        chunkData      = visitable.getChunkData();
        sourceIp       = visitable.getSourceIp();
        fileChunk      = new FileChunk(chunkData, sequenceNumber);

        if (fileDataMap.get(sourceIp) == null)
            errorMessage = "Not receiving a file from that ip";
        if (fileDataMap.get(sourceIp).putIfAbsent(sequenceNumber, fileChunk) != null)
            logger.logInternal(
                Constants.Strings.DISCARTED_CHUNK_FORMAT.formatted(sequenceNumber, chunkData.length)
            );

        if (errorMessage != null) {
            return ForeignResponseWrapper
                        .notAck(visitable.getMessageId())
                        .because(errorMessage)
                        .from(visitable.getSourceIp());
        }
        return ForeignResponseWrapper.ack(visitable.getMessageId()).from(visitable.getSourceIp());
    }

    @Override
    public ForeignResponseWrapper visit(ForeignEnd visitable) {
        String errorMessage;
        String receivedHash;
        String fileHash;
        InetAddress sourceIp;
        byte[] fileData;
        File file;

        log(visitable);

        errorMessage = null;
        receivedHash = visitable.getHash();
        sourceIp     = visitable.getSourceIp();
        file         = fileMap.get(sourceIp);

        if (fileDataMap.get(sourceIp) == null)
            errorMessage = "Not receiving a file from that ip";
        
        fileData = assembleFile(sourceIp);
        if (fileData == null) 
            errorMessage = "Missing chunks";

        try {
            fileHash = FileUitls.getFileHash(fileData);
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
            return ForeignResponseWrapper
                        .notAck(visitable.getMessageId())
                        .because(errorMessage)
                        .from(visitable.getSourceIp());
        }
        FileIO.writeFile(file.getName(), fileData);
        return ForeignResponseWrapper.ack(visitable.getMessageId()).from(visitable.getSourceIp());
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
    // Log only

    @Override
    public ForeignResponseWrapper visit(ForeignHeartbeat visitable) {
        log(visitable);
        return null;
    }

    @Override
    public ForeignResponseWrapper visit(ForeignTalk visitable) {
        log(visitable);
        return ForeignResponseWrapper.ack(visitable.getMessageId()).from(visitable.getSourceIp());
    }

    @Override
    public ForeignResponseWrapper visit(ForeignAck visitable) {
        log(visitable);
        return null;
    }

    @Override
    public ForeignResponseWrapper visit(ForeignNAck visitable) {
        log(visitable);
        return null;
    }
}
