package network.threads;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import interfaces.visitors.EncoderVisitor;
import messages.ThreadMessage;
import messages.foreign.*;
import messages.internal.InternalMessage;

import static utils.Constants.Configs.IP_ADDRESS;
import static utils.Constants.MessageHeaders.*;

public class MessageEncoder implements EncoderVisitor {

    // **************************************************************************************************************
    // Factory pattern for MessageEncoder

    public static MessageEncoder create() {
        return new MessageEncoder();
    }

    // **************************************************************************************************************
    // Visitor pattern implementation for encofing

    @Override
    public byte[] encode(ForeignAckMessage message) {
        /*
         * Header (char)
         * AckId  (int)
         */
        int        bufferSize = Character.BYTES + Integer.BYTES;
        ByteBuffer buffer     = ByteBuffer.allocate(bufferSize);

        buffer.putChar(ACK_HEADER);
        buffer.putInt(message.getAckkedId());

        return buffer.array();
    }

    @Override
    public byte[] encode(ForeignChunkMessage message) {
        /*
         * Header    (char)
         * MessageId (int)
         * SeqNum    (int)
         * Data      (byte[])
         */
        int        bufferSize = Character.BYTES + Integer.BYTES + Integer.BYTES + message.getData().length;
        ByteBuffer buffer     = ByteBuffer.allocate(bufferSize);

        buffer.putChar(CHUNK_HEADER);
        buffer.putInt(message.getMessageId());
        buffer.putInt(message.getSequenceNumber());
        buffer.put(message.getData());

        return buffer.array();
    }

    @Override
    public byte[] encode(ForeignEndMessage message) {
        /*
         * Header    (char)
         * MessageId (int)
         * Hash      (String)
         */
        byte[]     hashData   = message.getFileHash().getBytes(StandardCharsets.UTF_16BE);
        int        bufferSize = Character.BYTES + Integer.BYTES + hashData.length;
        ByteBuffer buffer     = ByteBuffer.allocate(bufferSize);

        buffer.putChar(END_HEADER);
        buffer.putInt(message.getMessageId());
        buffer.put(hashData);

        return buffer.array();
    }

    @Override
    public byte[] encode(ForeignFileMessage message) {
        /*
         * Header    (char)
         * MessageId (int)
         * NameSize  (int)
         * Name      (String)
         * Size      (long)
         */
        byte[]     nameData   = message.getFileName().getBytes(StandardCharsets.UTF_16BE);
        int        bufferSize = Character.BYTES + Integer.BYTES + Integer.BYTES + nameData.length + Long.BYTES;
        ByteBuffer buffer     = ByteBuffer.allocate(bufferSize);

        buffer.putChar(FILE_HEADER);
        buffer.putInt(message.getMessageId());
        buffer.putInt(nameData.length);
        buffer.put(nameData);
        buffer.putLong(message.getFileSize());

        return buffer.array();
    }

    @Override
    public byte[] encode(ForeignHeartbeatMessage message) {
        /*
         * Header    (char)
         * IpAddress (String)
         */
        byte[]     ipData     = IP_ADDRESS.getBytes(StandardCharsets.UTF_16BE);
        int        bufferSize = Character.BYTES + ipData.length;
        ByteBuffer buffer     = ByteBuffer.allocate(bufferSize);

        buffer.putChar(HEARTBEAT_HEADER);
        buffer.put(ipData);

        return buffer.array();
    }

    @Override
    public byte[] encode(ForeignNAckMessage message) {
        /*
         * Header (char)
         * NAckId (int)
         * Reason (String)
         */
        byte[]     reasonData = message.getReason().getBytes();
        int        bufferSize = Character.BYTES + Integer.BYTES + reasonData.length;
        ByteBuffer buffer     = ByteBuffer.allocate(bufferSize);

        buffer.putChar(NACK_HEADER);
        buffer.putInt(message.getNonAckkedId());
        buffer.put(reasonData);

        return buffer.array();
    }

    @Override
    public byte[] encode(ForeignTalkMessage message) {
        /*
         * Header    (char)
         * MessageId (int)
         * Content   (String)
         */
        byte[]     contentData = message.getContent().getBytes();
        int        bufferSize  = Character.BYTES + Integer.BYTES + contentData.length;
        ByteBuffer buffer      = ByteBuffer.allocate(bufferSize);

        buffer.putChar(TALK_HEADER);
        buffer.putInt(message.getMessageId());
        buffer.put(contentData);

        return buffer.array();
    }

    // **************************************************************************************************************

    public InternalMessage decodePacket(DatagramPacket packet) {
        ByteBuffer buffer;
        char header;
        
        buffer = ByteBuffer.wrap(packet.getData());
        header = buffer.getChar();

        return switch (header) {
            case HEARTBEAT_HEADER -> decodeHeartbeat(buffer, packet);
            case TALK_HEADER      -> decodeTalk(buffer, packet);
            case FILE_HEADER      -> decodeFile(buffer, packet);
            case CHUNK_HEADER     -> decodeChunk(buffer, packet);
            case END_HEADER       -> decodeEnd(buffer, packet);
            case ACK_HEADER       -> decodeAck(buffer, packet);
            case NACK_HEADER      -> decodeNAck(buffer, packet);
            default               -> unexpectedHeader(buffer, packet);
        };
    }

    private InternalMessage decodeAck(ByteBuffer buffer, DatagramPacket packet) {
        // TODO
        throw new UnsupportedOperationException("Not implemented Yet");
    }

    private InternalMessage decodeChunk(ByteBuffer buffer, DatagramPacket packet) {
        // TODO
        throw new UnsupportedOperationException("Not implemented Yet");
    }

    private InternalMessage decodeEnd(ByteBuffer buffer, DatagramPacket packet) {
        // TODO
        throw new UnsupportedOperationException("Not implemented Yet");
    }

    private InternalMessage decodeFile(ByteBuffer buffer, DatagramPacket packet) {
        // TODO
        throw new UnsupportedOperationException("Not implemented Yet");
    }

    private InternalMessage decodeHeartbeat(ByteBuffer buffer, DatagramPacket packet) {
        // TODO
        throw new UnsupportedOperationException("Not implemented Yet");
    }

    private InternalMessage decodeNAck(ByteBuffer buffer, DatagramPacket packet) {
        // TODO
        throw new UnsupportedOperationException("Not implemented Yet");
    }

    private InternalMessage decodeTalk(ByteBuffer buffer, DatagramPacket packet) {
        // TODO
        throw new UnsupportedOperationException("Not implemented Yet");
    }

    private InternalMessage unexpectedHeader(ByteBuffer buffer, DatagramPacket packet) {
        // TODO
        throw new UnsupportedOperationException("Not implemented Yet");
    }
}
