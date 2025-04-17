package network.messages;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TerminalIOMessage {
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
    // Factory pattern for IOTerminalMessage

    public static TerminalIOMessage exit() {
        return new TerminalIOMessage(IOTerminalType.EXIT, null, null);
    }

    private TerminalIOMessage(IOTerminalType type, String stringField, InetAddress destinationIp) {
        this.type          = type;
        this.stringField   = stringField;
        this.destinationIp = destinationIp;
    }

    // ****************************************************************************************************************
    // Builder pattern for IOTerminalMessage

    public static IOTerminalIpSetter sendMessage(String message) {
        return new Builder(IOTerminalType.SEND_MESSAGE, message);
    }

    public static IOTerminalIpSetter sendFile(String fileName) {
        return new Builder(IOTerminalType.SEND_FILE, fileName);
    }

    public interface IOTerminalIpSetter {
        TerminalIOMessage toIp(String ipAddress) throws UnknownHostException;
    }

    private static class Builder implements IOTerminalIpSetter {
        private IOTerminalType type;
        private String stringField;
        private InetAddress destinationIp;

        @Override
        public TerminalIOMessage toIp(String ipAddress) throws UnknownHostException {
            this.destinationIp = InetAddress.getByName(ipAddress);
            return new TerminalIOMessage(this);
        }

        private Builder(IOTerminalType type, String stringField) {
            this.type        = type;
            this.stringField = stringField;
        }
    }

    private TerminalIOMessage(Builder builder) {
        this.type          = builder.type;
        this.stringField   = builder.stringField;
        this.destinationIp = builder.destinationIp;
    }
}
