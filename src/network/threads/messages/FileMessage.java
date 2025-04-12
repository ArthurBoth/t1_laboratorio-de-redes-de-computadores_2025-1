package network.threads.messages;

import constants.Constants.Strings;

public class FileMessage extends ExternalMessage {
    private final long FILE_SIZE;
    private final String FILE_NAME;

    protected FileMessage(FileMessageBuilder builder) {
        super(builder);
        FILE_SIZE = builder.getFileSize();
        FILE_NAME = builder.getFileName();
    }
    
    @Override
    public byte[] getMessageBytes() {
        String message = String.format(Strings.FILE_FORMAT, ID, FILE_NAME, FILE_SIZE);
        return message.getBytes();
    }
}
