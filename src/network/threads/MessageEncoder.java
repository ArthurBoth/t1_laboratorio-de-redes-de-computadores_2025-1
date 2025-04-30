package network.threads;

import static utils.Constants.Configs.DEFAULT_PORT;
import static utils.Constants.Configs.IP_ADDRESS;
import static utils.Constants.Configs.PRINT_LOGS;
import static utils.Constants.MessageHeaders.*;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

import interfaces.visitors.EncoderVisitor;
import messages.ThreadMessage;
import messages.foreign.*;
import messages.internal.InternalMessage;
import utils.ConsoleLogger;
import utils.Constants.Configs;
import utils.FileUtils;

public class MessageEncoder implements EncoderVisitor {

    // ****************************************************************************************************
    // Factory pattern for MessageEncoder

    public static MessageEncoder create() {
        return new MessageEncoder();
    }

    // ****************************************************************************************************
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
        byte[]     hashData   = message.getFileHash().getBytes(Configs.CHAR_SET);
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
        byte[]     nameData   = message.getFileName().getBytes(Configs.CHAR_SET);
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
         * Header   (char)
         * NodeName (String)
         * NodePort (int)
         */
        byte[]     ipData     = IP_ADDRESS.getHostName().getBytes(Configs.CHAR_SET);
        int        bufferSize = Character.BYTES + ipData.length + Integer.BYTES;
        ByteBuffer buffer     = ByteBuffer.allocate(bufferSize);

        buffer.putChar(HEARTBEAT_HEADER);
        buffer.put(ipData);
        buffer.putInt(DEFAULT_PORT);

        return buffer.array();
    }

    @Override
    public byte[] encode(ForeignNAckMessage message) {
        /*
         * Header (char)
         * NAckId (int)
         * Reason (String)
         */
        byte[]     reasonData = message.getReason().getBytes(Configs.CHAR_SET);
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
        byte[]     contentData = message.getContent().getBytes(Configs.CHAR_SET);
        int        bufferSize  = Character.BYTES + Integer.BYTES + contentData.length;
        ByteBuffer buffer      = ByteBuffer.allocate(bufferSize);

        buffer.putChar(TALK_HEADER);
        buffer.putInt(message.getMessageId());
        buffer.put(contentData);

        return buffer.array();
    }

    // ****************************************************************************************************
    // Message decoding

    public InternalMessage decodePacket(DatagramPacket packet) {
        byte[] data;
        ByteBuffer buffer;
        char header;
        
        data = Arrays.copyOf(packet.getData(), packet.getLength());
        if (PRINT_LOGS) ConsoleLogger.logPurple(FileUtils.byteArrayToString(data));
        if (PRINT_LOGS) ConsoleLogger.logPurple("(%d bytes)".formatted(packet.getLength()));

        buffer = ByteBuffer.wrap(data);
        header = buffer.getChar();

        return switch (header) {
            case HEARTBEAT_HEADER -> decodeHeartbeat( buffer, packet.getAddress(), packet.getPort());
            case TALK_HEADER      -> decodeTalk(      buffer, packet.getAddress(), packet.getPort());
            case FILE_HEADER      -> decodeFile(      buffer, packet.getAddress(), packet.getPort());
            case CHUNK_HEADER     -> decodeChunk(     buffer, packet.getAddress(), packet.getPort());
            case END_HEADER       -> decodeEnd(       buffer, packet.getAddress(), packet.getPort());
            case ACK_HEADER       -> decodeAck(       buffer, packet.getAddress(), packet.getPort());
            case NACK_HEADER      -> decodeNAck(      buffer, packet.getAddress(), packet.getPort());
            default               -> unexpectedHeader(buffer, packet.getAddress(), packet.getPort());
        };
    }

    private InternalMessage decodeAck(
        ByteBuffer buffer, InetAddress ipAddress, int port
    ) {
        /*
         * AckId  (int)
         */
        int akkedId= buffer.getInt();

        return ThreadMessage.internalMessage(getClass())
                            .receivedMessage()
                            .ack(akkedId)
                            .from(ipAddress)
                            .at(port);
    }

    private InternalMessage decodeChunk(
        ByteBuffer buffer, InetAddress ipAddress, int port
    ) {
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
                            .from(ipAddress)
                            .at(port);
    }

    private InternalMessage decodeEnd(
        ByteBuffer buffer, InetAddress ipAddress, int port
    ) {
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
                            .end(new String(hashData, Configs.CHAR_SET))
                            .from(ipAddress)
                            .at(port);
    }

    private InternalMessage decodeFile(
        ByteBuffer buffer, InetAddress ipAddress, int port
    ) {
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
                            .file(new String(nameData, Configs.CHAR_SET))
                            .size(fileSize)
                            .from(ipAddress)
                            .at(port);
    }

    private InternalMessage decodeHeartbeat(
        ByteBuffer buffer, InetAddress ipAddress, int port
    ) {
        /*
         * NodeName (String)
         */

        byte[] nodeName = new byte[buffer.remaining()];
        buffer.get(nodeName);

        return ThreadMessage.internalMessage(getClass())
                            .receivedMessage()
                            .heartbeat()
                            .name(new String(nodeName, Configs.CHAR_SET))
                            .from(ipAddress)
                            .at(port);
    }

    private InternalMessage decodeNAck(
        ByteBuffer buffer, InetAddress ipAddress, int port
    ) {
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
                            .reason(new String(reasonData, Configs.CHAR_SET))
                            .from(ipAddress)
                            .at(port);
    }

    private InternalMessage decodeTalk(
        ByteBuffer buffer, InetAddress ipAddress, int port
    ) {
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
                            .talk(new String(contentData, Configs.CHAR_SET))
                            .from(ipAddress)
                            .at(port);
    }

    private InternalMessage unexpectedHeader(ByteBuffer buffer, InetAddress ipAddress, int port) {
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        return ThreadMessage.internalMessage(getClass())
                            .receivedMessage()
                            .unsupportedMessage(new String(data, Configs.CHAR_SET))
                            .from(ipAddress)
                            .at(port);
    }
}
