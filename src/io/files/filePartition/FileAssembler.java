package io.files.filePartition;

import static utils.Constants.Configs.Paths.RECEIVE_FOLDER_PATH;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.files.FileIo;
import utils.Constants;
import utils.Constants.Strings.Errors;
import utils.FileUtils;

public class FileAssembler {
    private final Path FILE_PATH;
    private final long FINAL_SIZE;

    private String errorMessage;
    private boolean isComplete;
    private int seqNumber;
    private long currentSize;
    private MessageDigest digestor;

    private FileAssembler(Path filePath, long fileSize) throws NoSuchAlgorithmException {
        this.FILE_PATH  = filePath;
        this.FINAL_SIZE = fileSize;

        this.seqNumber    = -1;
        this.currentSize  = 0;
        this.isComplete   = false;
        this.errorMessage = null;

        digestor = MessageDigest.getInstance(Constants.Configs.HASHING_ALGORITHM);
    }

    public static FileAssembler of(String fileName, int fileSize) throws NoSuchAlgorithmException {
        return of(fileName, (long) fileSize);
    }

    /**
     * Returns an instance of FileAssembler or {@code null} if the file cannot be created or is not writable.
     * @param fileName the name of the file (with extension) to be created inside the {@code receiveFiles} folder
     * @return An instance of FileAssembler or {@code null} if the file cannot be created
     * @throws NoSuchAlgorithmException if the hashing algorithm is not found
     */
    public static FileAssembler of(String fileName, long fileSize) throws NoSuchAlgorithmException {
        Path filePath = Path.of(RECEIVE_FOLDER_PATH + fileName);
        if (!FileUtils.canCreateFile(filePath.toFile())) return null;
        if (fileSize <= 0) return null;

        return new FileAssembler(filePath, fileSize);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isComplete() {
        return isComplete;
    }

    /**
     * Adds a packet to the file assembler.
     * If the sequence number of the packet is not equal to the expected sequence number, 
     * it will be ignored. <br></br>
     * In cases of errors, the {@code errorMessage} will be set accordingly.
     * @param packet The packet to add.
     * @return true if the packet was added successfully, false otherwise.
     */
    public boolean addPacket(int seqNumber, byte[] data) {
        if (isComplete) {
            errorMessage = Errors.COMPLETE_ASSEMBLER;
            return false;
        }
        if (data.length == 0) {
            errorMessage = Errors.EMPTY_PACKET;
            return false;
        }
        if (currentSize + data.length > FINAL_SIZE)  {
            errorMessage = Errors.FILE_TOO_LARGE;
            return false;
        }
        if (seqNumber != this.seqNumber + 1) {
            errorMessage = null; // Out of order packet
            return false;
        }

        currentSize += data.length;
        seqNumber++;
        digestor.update(data);
        FileIo.writeChunk(FILE_PATH, data);
        return true;
    }

    /**
     * Completes the file assembly process. <br></br>
     * If the file is already complete, has a different size than expected, or has a different hash than expected,
     * the {@code errorMessage} will be set accordingly.
     * @param incomingHash The expected hash of the file.
     * @return true if the file was assembled successfully, false otherwise.
     */
    public boolean completeCreation(String incomingHash) {
        if (isComplete) {
            errorMessage = Errors.FILE_ALREADY_COMPLETE;
            return false;
        }
        if (currentSize != FINAL_SIZE) {
            errorMessage = null; // Out of order packet
            return false;
        }

        isComplete = true;
        String fileHash = hashToString(digestor.digest());
        if (!fileHash.equalsIgnoreCase(incomingHash)) {
            errorMessage = Errors.DIFFERENT_HASHES;
            return false;
        }
        return true;
    }

    private String hashToString(byte[] hashBytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : hashBytes) {
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }
}
