package network.threads.messages;

public abstract class ThreadMessage {
    private static int idCounter = 0;

    // **************************************************************************
    // Inheritance
    public abstract MessageType getType();

    // **************************************************************************
    // Internal Messages
    protected enum InternalMessageType {
        EXIT {
            @Override
            public MessageType getMessageType() {
                return MessageType.EXIT;
            }
        };

        public abstract MessageType getMessageType();
    }

    public static InternalMessageSelection internalMessage() {
        return new InternalMessageBuilder();
    }

    public interface InternalMessageSelection {
        InternalMessage exit();
    }

    protected static class InternalMessageBuilder implements InternalMessageSelection {
        private InternalMessageType type;

        protected MessageType getType() {
            return type.getMessageType();
        }

        @Override
        public InternalMessage exit() {
            this.type = InternalMessageType.EXIT;
            return new InternalMessage(this);
        }
    }


    // **************************************************************************
    // External Messages
    protected enum ExternalMessageType {
        HEARTBEAT{
            @Override
            public MessageType getMessageType() {
                return MessageType.HEARTBEAT;
            }
        }, 
        TALK{
            @Override
            public MessageType getMessageType() {
                return MessageType.TALK;
            }
        }, 
        FILE{
            @Override
            public MessageType getMessageType() {
                return MessageType.FILE;
            }
        }, 
        CHUNK{
            @Override
            public MessageType getMessageType() {
                return MessageType.CHUNK;
            }
        }, 
        END{
            @Override
            public MessageType getMessageType() {
                return MessageType.END;
            }
        },
        ACK{
            @Override
            public MessageType getMessageType() {
                return MessageType.ACK;
            }
        },
        NACK{
            @Override
            public MessageType getMessageType() {
                return MessageType.NACK;
            }
        };
        
        public abstract MessageType getMessageType();
    }

    public static ExternalMessageSelection externalMessage() {
        return new ExternalMessageBuilder();
    }

    public interface ExternalMessageSelection {
        ExternalMessage heartbeat();
        IpSetter talk(String message);
        FileSizeSetter file(String fileName);
        ByteArraySetter chunk(int sequenceNumber);
        IpSetter end(String hash);
        IpSetter ack(int ackId);
        ReasonSetter nack(int nAckId);
    }

    public interface FileSizeSetter {
        IpSetter fileSize(long size);
    }
    public interface ByteArraySetter {
        IpSetter byteArray(byte[] data);
    }
    
    public interface ReasonSetter {
        IpSetter reason(String reason);
    }

    public interface IpSetter {
        ExternalMessage toIp(String ip);
    }

    public static class ExternalMessageBuilder 
                            implements 
                                ExternalMessageSelection, IpSetter {
        private ExternalMessageType type;
        private String destinationIp;
        private String stringField;

        @Override
        public ExternalMessage heartbeat() {
            this.type = ExternalMessageType.HEARTBEAT;
            return new ExternalMessage(this);
        }

        @Override
        public IpSetter talk(String message) {
            this.type        = ExternalMessageType.TALK;
            this.stringField = message;
            return this;
        }
        
        @Override
        public FileSizeSetter file(String filename) {
            this.type = ExternalMessageType.FILE;
            return new FileMessageBuilder(filename);
        };

        @Override 
        public ChunkMessageBuilder chunk(int sequenceNumber) {
            this.type = ExternalMessageType.CHUNK;
            return new ChunkMessageBuilder(sequenceNumber);
        }

        @Override
        public IpSetter end(String hash) {
            this.type        = ExternalMessageType.END;
            this.stringField = hash;
            return this;
        }

        @Override
        public IpSetter ack(int ackId) {
            this.type = ExternalMessageType.ACK;
            return new AckMessageBuilder(ackId);
        }

        @Override
        public ReasonSetter nack(int nAckId) {
            this.type = ExternalMessageType.NACK;
            return new NAckMessageBuilder(nAckId);
        }

        @Override
        public ExternalMessage toIp(String ip) {
            this.destinationIp = ip;
            return new ExternalMessage(this);
        }

        protected ExternalMessageType getType() {
            return type;
        }

        protected String getDestinationIp() {
            return destinationIp;
        }

        protected String getStringField() {
            return stringField;
        }

        protected int getId() {
            return idCounter++;
        }
    }

    public static class FileMessageBuilder 
                    extends ExternalMessageBuilder 
                    implements FileSizeSetter {
        private long fileSize;
        private String fileName;

        @Override
        public FileMessageBuilder fileSize(long size) {
            this.fileSize = size;
            return this;
        }

        @Override
        public ExternalMessage toIp(String ip) {
            super.destinationIp = ip;
            return new FileMessage(this);
        }

        protected long getFileSize() {
            return fileSize;
        }

        protected String getFileName() {
            return fileName;
        }

        public FileMessageBuilder(String fileName) {
            this.fileName = fileName;
        }
    }

    public static class AckMessageBuilder 
                    extends ExternalMessageBuilder {
        private int ackId;

        @Override
        public ExternalMessage toIp(String ip) {
            super.destinationIp = ip;
            return new AckMessage(this);
        }

        protected int getAckId() {
            return ackId;
        }

        public AckMessageBuilder(int ackId) {
            this.ackId = ackId;
        }
    }

    public static class NAckMessageBuilder
                    extends ExternalMessageBuilder 
                    implements ReasonSetter {
        private int nAckId;
        private String reason;
        
        @Override
        public ExternalMessage toIp(String ip) {
            super.destinationIp = ip;
            return new NAckMessage(this);
        }

        @Override
        public IpSetter reason(String reason) {
            this.reason = reason;
            return this;
        }

        protected int getNackId() {
            return nAckId;
        }

        protected String getReason() {
            return reason;
        }

        public NAckMessageBuilder(int nAckId) {
            this.nAckId = nAckId;
        }
    }

    public static class ChunkMessageBuilder 
                    extends ExternalMessageBuilder 
                    implements ByteArraySetter {
        private int sequenceNumber;
        private byte[] data;
        
        @Override
        public ExternalMessage toIp(String ip) {
            super.destinationIp = ip;
            return new ChunkMessage(this);
        }

        @Override
        public IpSetter byteArray(byte[] data) {
            this.data = data;
            return this;
        }

        protected int getSequenceNumber() {
            return sequenceNumber;
        }

        protected byte[] getData() {
            return data;
        }

        public ChunkMessageBuilder(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }
    }
}
