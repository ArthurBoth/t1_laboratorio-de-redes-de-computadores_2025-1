package io.files.filePartition;

import static utils.Constants.Configs.Paths.SEND_FOLDER_PATH;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.files.FileIo;
import utils.Constants;
import utils.Constants.Configs;

public class FileDisassembler {
    private final Path FILE_PATH;

    private boolean isComplete;
    private int offset;
    private MessageDigest digestor;

    private FileDisassembler(Path filePath) throws NoSuchAlgorithmException {
        this.FILE_PATH  = filePath;
        this.offset     = 0;
        this.isComplete = false;

        digestor = MessageDigest.getInstance(Constants.Configs.HASHING_ALGORITHM);
    }

    /**
     * Returns an instance of FileDisassembler or {@code null} if the file does not exist or is not readable.
     * @param fileName the name of the file (with extension) inside the {@code sendFiles} folder to disassemble
     * @return An instance of FileDisassembler or {@code null} if the file does not exist or is not readable
     * @throws NoSuchAlgorithmException if the hashing algorithm is not found
     */
    public static FileDisassembler of(String fileName) throws NoSuchAlgorithmException {
        Path filePath = Path.of(SEND_FOLDER_PATH + fileName);
        if (!filePath.toFile().exists()) return null;
        if (!filePath.toFile().isFile()) return null;
        if (!filePath.toFile().canRead()) return null;

        return new FileDisassembler(filePath);
    }

    public long getFileSize() {
        return FILE_PATH.toFile().length();
    }

    /**
     * Reads the next chunk of the file.
     * Sets {@code isComplete} to {@code true} if the end of the file is reached.
     * @return The next chunk of the file or {@code null} if the end of the file is reached.
     */
    public byte[] readChunk() {
        byte[] data;

        if (isComplete) return null;

        data = FileIo.readChunk(FILE_PATH, offset, Configs.MAX_CHUNK_SIZE);
        if (data != null) {
            digestor.update(data);
            offset += data.length;
        } else {
            isComplete = true;
        }
        return data;
    }

    public String computeHash() {
        isComplete = true;
        byte[] hash = digestor.digest();
        return hashToString(hash);
    }

    private String hashToString(byte[] hashBytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : hashBytes) {
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }
}
