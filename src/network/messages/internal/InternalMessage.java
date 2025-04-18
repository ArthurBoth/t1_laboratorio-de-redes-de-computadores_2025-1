package network.messages.internal;

import network.messages.ThreadMessage;

public abstract class InternalMessage extends ThreadMessage {
    
    // **************************************************************************************************************
    // Factory pattern for InternalMessage

    public static IONetworkMessage.MessageSelection ioToNetwork() {
        return new IONetworkMessage.Builder();
    }

    public static TerminalIOMessage.MessageSelection terminalToIO() {
        return new TerminalIOMessage.Builder();
    }

    public static NetworkIOMessage.MessageSelection networkToIO() {
        return new NetworkIOMessage.Builder();
    }
}
