package io.fileIO.filePartition;

import java.util.Queue;

public class FileData {
    private final Queue<byte[]> FULL_DATA;
    private final String FILE_HASH;
    private final int CHUNK_COUNT;

    public FileData(Queue<byte[]> fullData, String fileHash) {
        this.FULL_DATA   = fullData;
        this.FILE_HASH   = fileHash;
        this.CHUNK_COUNT = fullData.size();
    }

    public byte[] getChunk() {
        return FULL_DATA.poll();
    }

    public String getFileHash() {
        return FILE_HASH;
    }

    public int getChunkCount() {
        return CHUNK_COUNT;
    }
}
