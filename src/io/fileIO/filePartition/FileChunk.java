package io.fileIO.filePartition;

public class FileChunk implements Comparable<FileChunk> {
    private final byte[] CHUNK_DATA;
    private final int SEQ_NUMBER;
    private final int SIZE;
    
    public FileChunk(byte[] chunkData, int chunkSeqNumber) {
        this.CHUNK_DATA = chunkData;
        this.SEQ_NUMBER = chunkSeqNumber;
        this.SIZE       = chunkData.length;
    }

    public byte[] getChunkData() {
        return CHUNK_DATA;
    }

    public int getChunkSeqNumber() {
        return SEQ_NUMBER;
    }

    public int getChunkSize() {
        return SIZE;
    }

    @Override
    public int compareTo(FileChunk o) {
        if (o == null) throw new NullPointerException("Cannot compare to null object");
        if (this.SEQ_NUMBER < o.SEQ_NUMBER) return -1;
        if (this.SEQ_NUMBER > o.SEQ_NUMBER) return 1;
        return 0;
    }
}
