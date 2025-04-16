package network.messages;

public class ReceivedFileMessage extends ReceivedMessage{
    private final String FILE_NAME;
    private final long FILE_SIZE;
    
    protected ReceivedFileMessage(ReceivedFileMessageBuilder builder) {
        super(builder);
        FILE_NAME = builder.getFileName();
        FILE_SIZE = builder.getFileSize();
    }

    public String getFileName() {
        return FILE_NAME;
    }

    public long getFileSize() {
        return FILE_SIZE;
    }
}
