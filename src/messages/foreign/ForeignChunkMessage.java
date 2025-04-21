package messages.foreign;

import interfaces.visitors.ForeignMessageVisitor;
import utils.FileUtils;

import static utils.Constants.Strings.CHUNK_FORMAT;
import static utils.Constants.Strings.CHUNK_LOG_FORMAT;

public class ForeignChunkMessage extends ForeignMessage {
    private final int MESSAGE_ID;
    private int chunkNumber;
    private byte[] chunkData;

    // **************************************************************************************************************
    // Inherited fields from ForeignMessage

    @Override
    protected String assembleFormattedMessage() {
        String chunkContent = FileUtils.byteArrayToString(chunkData);
        return CHUNK_FORMAT.formatted(
                MESSAGE_ID,
                chunkNumber
                ) + chunkContent;
    }    

    // **************************************************************************************************************
    // Visitor pattern for ForeignChunkMessage

    @Override
    public void accept(ForeignMessageVisitor visitor) {
        visitor.visit(this);
    }

    // **************************************************************************************************************
    // Loggable interface implementation

    @Override
    public String getMessage() {
        return "(%s) %s".formatted(
            clazz.getSimpleName(),
            formattedMessage
            );
    }

    @Override
    public String getPrettyMessage() {
        return CHUNK_LOG_FORMAT.formatted(
            clazz.getSimpleName(),
            destinationIp.getHostAddress(),
            MESSAGE_ID,
            chunkNumber,
            FileUtils.byteArrayToString(chunkData),
            chunkData.length
            );
    }
    
    // **************************************************************************************************************
    // Builder pattern for ForeignChunkMessage

    public static ByteArraySetter create(Class<?> clazz, int messageId, int chunkNumber) {
        return new Builder(clazz, messageId, chunkNumber);
    }

    public interface ByteArraySetter {
        IpSetter<ForeignChunkMessage> data(byte[] chunkData);
    }

    private static class Builder extends IpBuilder<ForeignChunkMessage> implements ByteArraySetter {
        private final int MESSAGE_ID;
        private Class<?> clazz;
        private int chunkNumber;
        private byte[] chunkData;

        private Builder(Class<?> clazz, int messageId, int chunkNumber) {
            this.MESSAGE_ID  = messageId;
            this.clazz       = clazz;
            this.chunkNumber = chunkNumber;
        }

        @Override
        public IpSetter<ForeignChunkMessage> data(byte[] chunkData) {
            this.chunkData = chunkData;
            return this;
        }

        @Override
        protected ForeignChunkMessage self() {
            return new ForeignChunkMessage(this);
        }
    }

    private ForeignChunkMessage(Builder builder) {
        this.MESSAGE_ID  = builder.MESSAGE_ID;
        this.clazz       = builder.clazz;
        this.chunkNumber = builder.chunkNumber;
        this.chunkData   = builder.chunkData;
    }
}
