package network.messages;

import java.net.InetAddress;
import java.net.UnknownHostException;

import constants.Constants;
import network.messages.types.ExternalMessageType;
import network.messages.types.InternalMessageType;

public abstract class ThreadMessage {
    private static int idCounter = 0;

    // **************************************************************************
    // Inheritance
    public abstract boolean isExternalMessage();

    // **************************************************************************
    // Internal Messages

    public static InternalMessage decodeMessage(InetAddress sourceIp, byte[] data) {
        ExternalMessageType type = ExternalMessageType.decodeHeader(data[0]);
        switch (type) {
            case HEARTBEAT -> {
                return receivedMessage()
                        .heartbeat()
                        .sourceIp(sourceIp);
            }
            case TALK      -> {
                String[] splitMessage = new String(data, 1, data.length - 1).split(" ");
                int    messageId      = Integer.parseInt(splitMessage[0]);
                String message        = splitMessage[1];
                return receivedMessage()
                        .id(messageId)
                        .talk(message)
                        .sourceIp(sourceIp);
            }
            case FILE      -> {
                String[] splitMessage = new String(data, 1, data.length - 1).split(" ");
                int    messageId      = Integer.parseInt(splitMessage[0]);
                String fileName       = splitMessage[1];
                long   fileSize       = Long.parseLong(splitMessage[2]);
                return receivedMessage()
                        .id(messageId)
                        .file(fileName)
                        .size(fileSize)
                        .sourceIp(sourceIp);
            }
            case CHUNK     -> {
                String[] splitMessage = new String(data, 1, data.length - 1).split(" ");
                int    messageId      = Integer.parseInt(splitMessage[0]);
                int    sequenceNumber = Integer.parseInt(splitMessage[1]);
                byte  [] chunkData    = splitMessage[2].getBytes();
                return receivedMessage()
                        .id(messageId)
                        .chunk(sequenceNumber)
                        .data(chunkData)
                        .sourceIp(sourceIp);
            }
            case END       -> {
                String[] splitMessage = new String(data, 1, data.length - 1).split(" ");
                int    messageId      = Integer.parseInt(splitMessage[0]);
                String hash           = splitMessage[1];
                return receivedMessage()
                        .id(messageId)
                        .end(hash)
                        .sourceIp(sourceIp);
            }
            case ACK       -> {
                String[] splitMessage = new String(data, 1, data.length - 1).split(" ");
                int    ackId          = Integer.parseInt(splitMessage[0]);
                return receivedMessage()
                        .ack(ackId)
                        .sourceIp(sourceIp);
            }
            case NACK      -> {
                String[] splitMessage = new String(data, 1, data.length - 1).split(" ");
                int    nAckId         = Integer.parseInt(splitMessage[0]);
                String reason         = splitMessage[1];
                return receivedMessage()
                        .nack(nAckId)
                        .reason(reason)
                        .sourceIp(sourceIp);
            }
            default        -> {
                return null;
            }
        }
    }

    private static ReceivedIdSetter receivedMessage() {
        return new ReceivedMessageBuilder();
    }

    public static InternalMessageSelection internalMessage() {
        return new InternalMessageBuilder();
    }

    public interface InternalMessageSelection {
        InternalMessage exit();
    }

    private interface ReceivedIdSetter {
        ReceivedMessageSelection id(int id);
        SourceIpSetter heartbeat();
        SourceIpSetter ack(int ackId);
        ReceivedReasonSetter nack(int nAckId);
    }

    private interface ReceivedMessageSelection {
        SourceIpSetter talk(String message);
        ReceivedFileSizeSetter file(String fileName);
        ReceivedByteArraySetter chunk(int sequenceNumber);
        SourceIpSetter end(String hash);
    }

    private interface ReceivedFileSizeSetter {
        SourceIpSetter size(long size);
    }

    private interface ReceivedByteArraySetter {
        SourceIpSetter data(byte[] data);
    }

    private interface ReceivedReasonSetter {
        SourceIpSetter reason(String reason);
    }

    private interface SourceIpSetter {
        ReceivedMessage sourceIp(InetAddress ip);
    }

    protected static class InternalMessageBuilder implements InternalMessageSelection {
        private InternalMessageType type;

        protected InternalMessageType getType() {
            return type;
        }

        @Override
        public InternalMessage exit() {
            this.type = InternalMessageType.EXIT;
            return new InternalMessage(this);
        }
    }

    protected static class ReceivedMessageBuilder 
                    extends InternalMessageBuilder
                    implements ReceivedIdSetter, ReceivedMessageSelection, SourceIpSetter {
        private int messageId;
        private InetAddress sourceIp;
        private InternalMessageType type;
        private String stringField; 

        protected int getMessageId() {
            return messageId;
        }

        protected InetAddress getSourceIp() {
            return sourceIp;
        }

        protected InternalMessageType getType() {
            return type;
        }

        protected String getStringField() {
            return stringField;
        }

        @Override
        public ReceivedMessageSelection id(int id) {
            this.messageId = id;
            return this;
        }

        @Override
        public SourceIpSetter heartbeat() {
            this.type = InternalMessageType.RECEIVED_HEARTBEAT;
            return this;
        }

        @Override
        public SourceIpSetter talk(String message) {
            this.type = InternalMessageType.RECEIVED_TALK;
            this.stringField = message;
            return this;
        }

        @Override
        public SourceIpSetter end(String hash) {
            this.type = InternalMessageType.RECEIVED_END;
            this.stringField = hash;
            return this;
        }

        @Override
        public ReceivedMessage sourceIp(InetAddress ip) {
            this.sourceIp = ip;
            return new ReceivedMessage(this);
        }

        @Override
        public ReceivedFileSizeSetter file(String fileName) {
            this.type = InternalMessageType.RECEIVED_FILE;
            return new ReceivedFileMessageBuilder(fileName);
        }

        @Override
        public ReceivedByteArraySetter chunk(int sequenceNumber) {
            this.type = InternalMessageType.RECEIVED_CHUNK;
            return new ReceivedChunkMessageBuilder(sequenceNumber);
        }

        @Override
        public SourceIpSetter ack(int ackId) {
            this.type = InternalMessageType.RECEIVED_ACK;
            return new ReceivedAckMessageBuilder(ackId);
        }

        @Override
        public ReceivedReasonSetter nack(int nAckId) {
            this.type = InternalMessageType.RECEIVED_NACK;
            return new ReceivedNAckMessageBuilder(nAckId);
        }
    }

    protected static class ReceivedFileMessageBuilder 
                    extends ReceivedMessageBuilder
                    implements ReceivedFileSizeSetter {
        private long fileSize;
        private String fileName;

        protected String getFileName() {
            return fileName;
        }

        protected long getFileSize() {
            return fileSize;
        }

        @Override
        public SourceIpSetter size(long size) {
            this.fileSize = size;
            return this;
        }

        @Override
        public ReceivedMessage sourceIp(InetAddress ip) {
            super.sourceIp = ip;
            return new ReceivedFileMessage(this);
        }

        public ReceivedFileMessageBuilder(String fileName) {
            this.fileName = fileName;
        }
    }

    protected static class ReceivedChunkMessageBuilder 
                    extends ReceivedMessageBuilder 
                    implements ReceivedByteArraySetter {
        private int sequenceNumber;
        private byte[] data;

        protected int getSequenceNumber() {
            return sequenceNumber;
        }

        protected byte[] getData() {
            return data;
        }

        @Override
        public ReceivedMessage sourceIp(InetAddress ip) {
            super.sourceIp = ip;
            return new ReceivedChunkMessage(this);
        }

        @Override
        public SourceIpSetter data(byte[] data) {
            this.data = data;
            return this;
        }

        public ReceivedChunkMessageBuilder(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }
    }

    protected static class ReceivedAckMessageBuilder 
                    extends ReceivedMessageBuilder {
        private int ackId;

        protected int getAckId() {
            return ackId;
        }

        @Override
        public ReceivedMessage sourceIp(InetAddress ip) {
            super.sourceIp = ip;
            return new ReceivedAckMessage(this);
        }

        public ReceivedAckMessageBuilder(int ackId) {
            this.ackId = ackId;
        }
    }

    protected static class ReceivedNAckMessageBuilder 
                    extends ReceivedMessageBuilder 
                    implements ReceivedReasonSetter {
        private int nAckId;
        private String reason;

        protected int getNackId() {
            return nAckId;
        }

        protected String getReason() {
            return reason;
        }

        @Override
        public ReceivedMessage sourceIp(InetAddress ip) {
            super.sourceIp = ip;
            return new ReceivedNAckMessage(this);
        }

        @Override
        public SourceIpSetter reason(String reason) {
            this.reason = reason;
            return this;
        }

        public ReceivedNAckMessageBuilder(int nAckId) {
            this.nAckId = nAckId;
        }
    }

    // **************************************************************************
    // External Messages

    public static ExternalMessageSelection externalMessage() {
        return new ExternalMessageBuilder();
    }

    public interface ExternalMessageSelection {
        ExternalMessage heartbeat() throws UnknownHostException;
        DestinationIpSetter talk(String message);
        SentFileSizeSetter file(String fileName);
        SentByteArraySetter chunk(int sequenceNumber);
        DestinationIpSetter end(String hash);
        DestinationIpSetter ack(int ackId);
        SentReasonSetter nack(int nAckId);
    }

    public interface SentFileSizeSetter {
        DestinationIpSetter fileSize(long size);
    }
    public interface SentByteArraySetter {
        DestinationIpSetter data(byte[] data);
    }
    
    public interface SentReasonSetter {
        DestinationIpSetter reason(String reason);
    }

    public interface DestinationIpSetter {
        ExternalMessage toIp(String ip) throws UnknownHostException;
    }

    protected static class ExternalMessageBuilder 
                            implements 
                                ExternalMessageSelection, DestinationIpSetter {
        private ExternalMessageType type;
        private InetAddress destinationIp;
        private String stringField;
        
        protected ExternalMessageType getType() {
            return type;
        }

        protected InetAddress getDestinationIp() {
            return destinationIp;
        }

        protected String getStringField() {
            return stringField;
        }

        protected int getId() {
            return idCounter++;
        }

        @Override
        public ExternalMessage heartbeat() throws UnknownHostException {
            this.type = ExternalMessageType.HEARTBEAT;
            this.destinationIp = InetAddress.getByName(Constants.Strings.BROADCAST_IP);
            return new ExternalMessage(this);
        }

        @Override
        public DestinationIpSetter talk(String message) {
            this.type        = ExternalMessageType.TALK;
            this.stringField = message;
            return this;
        }
        
        @Override
        public SentFileSizeSetter file(String filename) {
            this.type = ExternalMessageType.FILE;
            return new SentFileMessageBuilder(filename);
        };

        @Override 
        public SentChunkMessageBuilder chunk(int sequenceNumber) {
            this.type = ExternalMessageType.CHUNK;
            return new SentChunkMessageBuilder(sequenceNumber);
        }

        @Override
        public DestinationIpSetter end(String hash) {
            this.type        = ExternalMessageType.END;
            this.stringField = hash;
            return this;
        }

        @Override
        public DestinationIpSetter ack(int ackId) {
            this.type = ExternalMessageType.ACK;
            return new SentAckMessageBuilder(ackId);
        }

        @Override
        public SentReasonSetter nack(int nAckId) {
            this.type = ExternalMessageType.NACK;
            return new SentNAckMessageBuilder(nAckId);
        }

        @Override
        public ExternalMessage toIp(String ip) throws UnknownHostException{
            this.destinationIp = InetAddress.getByName(ip);
            return new ExternalMessage(this);
        }
    }

    protected static class SentFileMessageBuilder 
                    extends ExternalMessageBuilder 
                    implements SentFileSizeSetter {
        private long fileSize;
        private String fileName;

        @Override
        public SentFileMessageBuilder fileSize(long size) {
            this.fileSize = size;
            return this;
        }

        @Override
        public ExternalMessage toIp(String ip) throws UnknownHostException {
            super.destinationIp = InetAddress.getByName(ip);
            return new SentFileMessage(this);
        }

        protected long getFileSize() {
            return fileSize;
        }

        protected String getFileName() {
            return fileName;
        }

        public SentFileMessageBuilder(String fileName) {
            this.fileName = fileName;
        }
    }

    protected static class SentChunkMessageBuilder 
                    extends ExternalMessageBuilder 
                    implements SentByteArraySetter {
        private int sequenceNumber;
        private byte[] data;
        
        @Override
        public ExternalMessage toIp(String ip) throws UnknownHostException {
            super.destinationIp = InetAddress.getByName(ip);
            return new SentChunkMessage(this);
        }

        @Override
        public DestinationIpSetter data(byte[] data) {
            this.data = data;
            return this;
        }

        protected int getSequenceNumber() {
            return sequenceNumber;
        }

        protected byte[] getData() {
            return data;
        }

        public SentChunkMessageBuilder(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }
    }

    protected static class SentAckMessageBuilder 
                    extends ExternalMessageBuilder {
        private int ackId;

        @Override
        public ExternalMessage toIp(String ip) throws UnknownHostException {
            super.destinationIp = InetAddress.getByName(ip);
            return new SentAckMessage(this);
        }

        protected int getAckId() {
            return ackId;
        }

        public SentAckMessageBuilder(int ackId) {
            this.ackId = ackId;
        }
    }

    protected static class SentNAckMessageBuilder
                    extends ExternalMessageBuilder 
                    implements SentReasonSetter {
        private int nAckId;
        private String reason;
        
        @Override
        public ExternalMessage toIp(String ip) throws UnknownHostException {
            super.destinationIp = InetAddress.getByName(ip);
            return new SentNAckMessage(this);
        }

        @Override
        public DestinationIpSetter reason(String reason) {
            this.reason = reason;
            return this;
        }

        protected int getNackId() {
            return nAckId;
        }

        protected String getReason() {
            return reason;
        }

        public SentNAckMessageBuilder(int nAckId) {
            this.nAckId = nAckId;
        }
    }
}
