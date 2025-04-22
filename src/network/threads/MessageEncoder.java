package network.threads;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import interfaces.visitors.EncoderVisitor;
import io.consoleIO.ConsoleLogger;
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
    // Message decoding

    public InternalMessage decodePacket(DatagramPacket packet) {
        ByteBuffer buffer;
        char header;
        
        buffer = ByteBuffer.wrap(packet.getData());
        header = buffer.getChar();

        return switch (header) {
            case HEARTBEAT_HEADER -> decodeHeartbeat(buffer, packet.getAddress());
            case TALK_HEADER      -> decodeTalk(buffer, packet.getAddress());
            case FILE_HEADER      -> decodeFile(buffer, packet.getAddress());
            case CHUNK_HEADER     -> decodeChunk(buffer, packet.getAddress());
            case END_HEADER       -> decodeEnd(buffer, packet.getAddress());
            case ACK_HEADER       -> decodeAck(buffer, packet.getAddress());
            case NACK_HEADER      -> decodeNAck(buffer, packet.getAddress());
            default               -> unexpectedHeader(buffer, packet.getAddress());
        };
    }

    private InternalMessage decodeAck(ByteBuffer buffer, InetAddress ipAddress) {
        /*
         * AckId  (int)
         */
        int akkedId= buffer.getInt();

        return ThreadMessage.internalMessage(getClass())
                            .receivedMessage()
                            .ack(akkedId)
                            .from(ipAddress);
    }

    private InternalMessage decodeChunk(ByteBuffer buffer, InetAddress ipAddress) {
        /*
         * MessageId (int)
         * SeqNum    (int)
         * Data      (byte[])
         */
        int messageId;
        int sequenceNumber;
        byte[] chunkData;

        messageId      = buffer.getInt();
        sequenceNumber = buffer.getInt();
        chunkData      = new byte[buffer.remaining()];
        buffer.get(chunkData);

        return ThreadMessage.internalMessage(getClass())
                            .receivedMessage(messageId)
                            .chunk(chunkData)
                            .sequenceNumber(sequenceNumber)
                            .from(ipAddress);
    }

    private InternalMessage decodeEnd(ByteBuffer buffer, InetAddress ipAddress) {
        /*
         * MessageId (int)
         * Hash      (String)
         */
        int messageId;
        byte[] hashData;

        messageId = buffer.getInt();
        hashData  = new byte[buffer.remaining()];
        buffer.get(hashData);

        return ThreadMessage.internalMessage(getClass())
                            .receivedMessage(messageId)
                            .end(new String(hashData, StandardCharsets.UTF_16BE))
                            .from(ipAddress);
    }

    private InternalMessage decodeFile(ByteBuffer buffer, InetAddress ipAddress) {
        /*
         * MessageId (int)
         * NameSize  (int)
         * Name      (String)
         * Size      (long)
         */
        int messageId;
        int nameSize;
        byte[] nameData;
        long fileSize;

        messageId = buffer.getInt();
        nameSize  = buffer.getInt();
        nameData  = new byte[nameSize];
        buffer.get(nameData, 0, nameSize);
        fileSize  = buffer.getLong();

        return ThreadMessage.internalMessage(getClass())
                            .receivedMessage(messageId)
                            .file(new String(nameData, StandardCharsets.UTF_16BE))
                            .size(fileSize)
                            .from(ipAddress);
    }

    private InternalMessage decodeHeartbeat(ByteBuffer buffer, InetAddress ipAddress) {
        /*
         * IpAddress (String)
         */

        byte[] ipData = new byte[buffer.remaining()];
        buffer.get(ipData);

        try {
            return ThreadMessage.internalMessage(getClass())
                                .receivedMessage()
                                .heartbeat()
                                .from(new String(ipData, StandardCharsets.UTF_16BE));
        } catch (UnknownHostException e) {
            ConsoleLogger.logError("Unknow host, Packet's address", e);
            return ThreadMessage.internalMessage(getClass())
                                .receivedMessage()
                                .heartbeat()
                                .from(ipAddress);
        }
    }

    private InternalMessage decodeNAck(ByteBuffer buffer, InetAddress ipAddress) {
        /*
         * NAckId (int)
         * Reason (String)
         */
        int nAckkedId;
        byte[] reasonData;

        nAckkedId  = buffer.getInt();
        reasonData = new byte[buffer.remaining()];
        buffer.get(reasonData);

        return ThreadMessage.internalMessage(getClass())
                            .receivedMessage()
                            .nAck(nAckkedId)
                            .reason(new String(reasonData, StandardCharsets.UTF_16BE))
                            .from(ipAddress);
    }

    private InternalMessage decodeTalk(ByteBuffer buffer, InetAddress ipAddress) {
        /*
         * MessageId (int)
         * Content   (String)
         */
        int messageId;
        byte[] contentData;

        messageId   = buffer.getInt();
        contentData = new byte[buffer.remaining()];
        buffer.get(contentData);

        return ThreadMessage.internalMessage(getClass())
                            .receivedMessage(messageId)
                            .talk(new String(contentData, StandardCharsets.UTF_16BE))
                            .from(ipAddress);
    }

    private InternalMessage unexpectedHeader(ByteBuffer buffer, InetAddress ipAddress) {
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        return ThreadMessage.internalMessage(getClass())
                            .receivedMessage()
                            .unsupportedMessage(new String(data, StandardCharsets.UTF_16BE))
                            .from(ipAddress);
    }
}
