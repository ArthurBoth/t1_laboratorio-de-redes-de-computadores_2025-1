package io.fileIO;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.Queue;

import constants.Constants;
import constants.Exceptions.FileSearchException;

public class FileManager {
    private FileLogger logger;

    public FileManager() {
        logger = new FileLogger();
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
        byte[] hashBytes;

        try (Formatter formatter = new Formatter()) {
            hashBytes = FileIO.hashFile(file.toPath());
            for (byte b : hashBytes) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new FileSearchException("Hash algorithm not found", e);
        }
    }
}
