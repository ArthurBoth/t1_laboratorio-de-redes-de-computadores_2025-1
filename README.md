# Protocol definition

This application uses a custom communication protocol for communication with other systems.

## Message headers

For external communication, the application uses symbols as message headers.

They are:

- ♥ for HEARTBEAT  <!-- Alt + 3    -->
- ¶ for TALK       <!-- Alt + 0182 -->
- ├ for FILE       <!-- Alt + 195  -->
- ─ for CHUNK      <!-- Alt + 196  -->
- ┤ for END        <!-- Alt + 180  -->
- ­­= for ACK        <!-- Equal      -->
- ¬ for NACK       <!-- Alt + 170  -->

## Message format

Messages will always have their first byte as the header symbol, followed by the message content.
The message is encodeed in bytes, and must be decoded in the same way

The content varies depending on the message type, however all messages are encoded based on simple Java types.
The content is encoded in the following way:

- `Characters` and `Strings` are encoded as UTF_16BE bytes.
- `Integers` are signed and encoded as 4 bytes, in big endian format.
- `Longs` are signed and encoded as 8 bytes, in big endian format.

### HEARTBEAT

The HEARTBEAT message is used to check if the connection is alive.
It's content is the machine's name in the network (`String`) and they're sent every few seconds.

example:

```PlainText
♥192.168.0.1
```

### TALK

The TALK message is used to send a written message to another machine.
It's content is an ID, followed by a string, which is the 'talk content' (`String`) itself.

example:

```PlainText
¶1Hello, world!
```

### FILE

The FILE message is used to request sending a file to another machine.
It's content is an ID, followed by an `Integer` (which is the size of the incoming file name in bytes),
followed by the file name (`String`) and the file size (`Long`).

example:

```PlainText
├2|16file.txt1024
```

(Note that an actual message does not have a `|`, it was added to aid in visual distinction)

### CHUNK

The CHUNK message is used to send a chunk of a file to another machine.
It's content is an ID, followed by a space and the chunk number and byte sequence.

The chunk number is an sequential integer, that represents the order of the chunk in the file.

example:

```PlainText
─2 1 0x00 0x01 0x02 0x03 0x04 0x05 0x06 0x07 0x08 0x09
```

### END

The END message is used to signal the end of a file transfer.
It's content is an ID, followed by a space and a hash of the file.
The hash is used to verify the integrity of the file.

The hash is a SHA-256 hash of the file content, encoded in hexadecimal.

example:

```PlainText
┤2 0x1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef
```

### ACK

The ACK message is used to acknowledge the receipt of a message.
It's content just the ID of the message that was received.

The ACK message is sent as a response to a TALK, FILE, CHUNK or END message.

example:

```PlainText
­­=1
```

### NACK

The NACK message is used to signal that a message was not received correctly.
It's content is the ID of the message that was not received correctly, followed by a space and the reason for the NACK.

The reason is a string that describes the error.

example:

```PlainText
¬1 Invalid message format
```
