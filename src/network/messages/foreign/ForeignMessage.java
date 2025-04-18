package network.messages.foreign;

import java.net.InetAddress;

import interfaces.ForeignLoggable;
import interfaces.visitors.ForeignMessageVisitor;
import network.messages.ThreadMessage;

public abstract class ForeignMessage extends ThreadMessage implements ForeignLoggable {
    protected InetAddress sourceIp;
    protected String decodedMessage;

    public InetAddress getSourceIp() {
        return sourceIp;
    }

    // ****************************************************************************************************************
    // Visitor pattern for ForeignMessage

    public abstract ForeignResponseWrapper accept(ForeignMessageVisitor visitor);
    
    // ****************************************************************************************************************
    // Factory pattern for ForeignMessage

    public static ForeignHeartbeat.DecodedMessageSetter heartbeat(InetAddress sourceIp) {
        return new ForeignHeartbeat.Builder(sourceIp);
    }

    public static ForeignTalk.IdSetter talk(InetAddress sourceIp) {
        return new ForeignTalk.Builder(sourceIp);
    }

    public static ForeignFile.IdSetter file(InetAddress sourceIp) {
        return new ForeignFile.Builder(sourceIp);
    }

    public static ForeignChunk.IdSetter chunk(InetAddress sourceIp) {
        return new ForeignChunk.Builder(sourceIp);
    }

    public static ForeignEnd.IdSetter end(InetAddress sourceIp) {
        return new ForeignEnd.Builder(sourceIp);
    }

    public static ForeignAck.AckIdSetter ack(InetAddress sourceIp) {
        return new ForeignAck.Builder(sourceIp);
    }

    public static ForeignNAck.NotAckIdSetter nack(InetAddress sourceIp) {
        return new ForeignNAck.Builder(sourceIp);
    }
}