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
        // return new ExternalMessageBuilder();
        // TODO
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public interface ExternalMessageSelection {
        // TODO
        // ExternalMessage heartbeat();
        // ExternalMessage talk();
        // ExternalMessage file();
        // ExternalMessage chunk();
        // ExternalMessage end();
        // ExternalMessage ack();
        // ExternalMessage nack();
    }
}
