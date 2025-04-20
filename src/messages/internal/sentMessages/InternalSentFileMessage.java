package messages.internal.sentMessages;

import interfaces.visitors.InternalMessageVisitor;

import static utils.Constants.Configs.MAX_CHUNK_SIZE;
import static utils.Constants.Strings.FILE_SENDING_REQUEST_FORMAT;

public class InternalSentFileMessage extends InternalSentMessage {
    private String fileName;
    private String fileHash;
    private byte[] fileData;
    private int chunkCount;

    public long getFileSize() {
        return fileData.length;
    }

    public String getFileHash() {
        return fileHash;
    }

    public byte[][] getSplitData() {
        byte[][] splitData = new byte[chunkCount][];
        byte[] chunkData;
        int index, position;

        index    = 0;
        position = 0;
        for (; index < chunkCount - 1; index++) {
            chunkData = new byte[MAX_CHUNK_SIZE];
            System.arraycopy(fileData, position, chunkData, 0, MAX_CHUNK_SIZE);
            position = (index + 1) * MAX_CHUNK_SIZE;
            splitData[index] = chunkData;
        }
        chunkData = new byte[fileData.length - position];
        System.arraycopy(fileData, position, chunkData, 0, fileData.length - position);
        splitData[index] = chunkData;

        return splitData;
    }

    public String getFileName() {
        return fileName;
    }

    // **************************************************************************************************************
    // Visitor pattern for InternalSentFileMessage

    @Override
    public void accept(InternalMessageVisitor visitor) {
        visitor.visit(this);
    }

    // **************************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return FILE_SENDING_REQUEST_FORMAT.formatted(
            clazz.getSimpleName(), 
            fileName
            );
    }

    // **************************************************************************************************************
    // Builder pattern for InternalSentFileMessage

    public static IpBuilder<InternalSentFileMessage> create(Class<?> clazz, String fileName) {
        return new MandatoryBuilder(clazz, fileName);
    }

    private static class MandatoryBuilder extends IpBuilder<InternalSentFileMessage> {
        private Class<?> clazz;
        private String fileName;

        private MandatoryBuilder(Class<?> clazz, String fileName) {
            this.clazz    = clazz;
            this.fileName = fileName;
        }

        @Override
        protected InternalSentFileMessage self() {
            return new InternalSentFileMessage(this);
        }
    }

    private InternalSentFileMessage(MandatoryBuilder builder) {
        this.clazz         = builder.clazz;
        this.destinationIp = builder.destinationIp;
        this.fileName      = builder.fileName;
    }

    // *******************************************************
    // Builder Appenders

    // non-static method
    public FileHashSetter fileData(byte[] fileData) {
        return new OptionalBuilder(this, fileData);
    }

    public interface FileHashSetter {
        InternalSentFileMessage fileHash(String fileHash);
    }

    private static class OptionalBuilder implements FileHashSetter {
        InternalSentFileMessage other;
        protected String fileHash;
        protected byte[] fileData;
        protected int chunkCount;

        private OptionalBuilder(InternalSentFileMessage other, byte[] fileData) {
            this.other = other;
            this.fileData   = fileData;
            this.chunkCount = fileData.length / MAX_CHUNK_SIZE + (fileData.length % MAX_CHUNK_SIZE == 0 ? 0 : 1);
        }

        @Override
        public InternalSentFileMessage fileHash(String fileHash) {
            this.fileHash = fileHash;
            return new InternalSentFileMessage(other, this);
        }
    }

    private InternalSentFileMessage(InternalSentFileMessage other, OptionalBuilder optionalBuilder) {
        this.clazz         = other.clazz;
        this.destinationIp = other.destinationIp;
        this.fileName      = other.fileName;
        this.fileHash      = optionalBuilder.fileHash;
        this.fileData      = optionalBuilder.fileData;
        this.chunkCount    = optionalBuilder.chunkCount;
    }
}
