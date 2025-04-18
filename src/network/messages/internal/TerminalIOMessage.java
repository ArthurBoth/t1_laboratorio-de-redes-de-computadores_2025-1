package network.messages.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TerminalIOMessage extends InternalMessage {
    public enum IOTerminalType {
        EXIT, SEND_MESSAGE, SEND_FILE;
    }

    private IOTerminalType type;
    private String stringField;
    private InetAddress destinationIp;

    public IOTerminalType getType() {
        return type;
    }
    
    public String getStringField() {
        return stringField;
    }

    public InetAddress getDestinationIp() {
        return destinationIp;
    }

    // ****************************************************************************************************************
    // Builder pattern for TerminalIOMessage

    public interface MessageSelection {
        TerminalIOMessage exit();
        IOTerminalIpSetter sendMessage(String message);
        IOTerminalIpSetter sendFile(String fileName);
    }

    public interface IOTerminalIpSetter {
        TerminalIOMessage toIp(String ipAddress) throws UnknownHostException;
    }

    protected static class Builder implements MessageSelection, IOTerminalIpSetter {
        private IOTerminalType type;
        private String stringField;
        private InetAddress destinationIp;

        protected Builder() {
            this.type          = null;
            this.stringField   = null;
            this.destinationIp = null;
        }

        @Override
        public TerminalIOMessage exit() {
            this.type = IOTerminalType.EXIT;
            return new TerminalIOMessage(this);
        }

        @Override
        public IOTerminalIpSetter sendMessage(String message) {
            this.type = IOTerminalType.SEND_MESSAGE;
            return this;
        }

        @Override
        public IOTerminalIpSetter sendFile(String fileName) {
            this.type = IOTerminalType.SEND_FILE;
            return this;
        }

        @Override
        public TerminalIOMessage toIp(String ipAddress) throws UnknownHostException {
            this.destinationIp = InetAddress.getByName(ipAddress);
            return new TerminalIOMessage(this);
        }
    }

    private TerminalIOMessage(Builder builder) {
        this.type          = builder.type;
        this.stringField   = builder.stringField;
        this.destinationIp = builder.destinationIp;
    }

    // ***************************************************************************************************************
    // Loggable interface for InternalMessage

    @Override
    public String getActualMessage() {
        return type.toString();
    }
}
