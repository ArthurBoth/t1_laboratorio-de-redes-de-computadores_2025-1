package network.messages;

import java.net.InetAddress;

public class ReceivedMessage extends InternalMessage {
    private final InetAddress SOUREC_IP;
    private final int MESSAGE_ID;

    protected ReceivedMessage(ReceivedMessageBuilder builder) {
        super(builder);
        SOUREC_IP  = builder.getSourceIp();
        MESSAGE_ID = builder.getMessageId();
    }

    public int getId() {
        return MESSAGE_ID;
    }
    
    public final String getSourceIp() {
        return SOUREC_IP.getHostAddress();
    }
}
