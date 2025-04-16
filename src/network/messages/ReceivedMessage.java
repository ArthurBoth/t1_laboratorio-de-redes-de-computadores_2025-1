package network.messages;

import java.net.InetAddress;

public class ReceivedMessage extends InternalMessage {
    private final int MESSAGE_ID;
    private final InetAddress SOUREC_IP;

    protected ReceivedMessage(ReceivedMessageBuilder builder) {
        super(builder);
        SOUREC_IP  = builder.getSourceIp();
        MESSAGE_ID = builder.getMessageId();
    }

    public final int getId() {
        return MESSAGE_ID;
    }
    
    public final String getSourceIp() {
        return SOUREC_IP.getHostAddress();
    }
}
