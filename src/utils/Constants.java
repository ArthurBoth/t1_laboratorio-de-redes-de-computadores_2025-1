package utils;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public final class Constants {
    public static final class Configs {
        public static final int DEFAULT_PORT     = 9000;
        public static final int MAX_MESSAGE_SIZE = 1 << 14;                                            // 16 KB
        public static final int MAX_CHUNK_SIZE   = CompilingFunctions.calculateMinimumMaxChunkSize();

        public static final int SOCKET_TIMEOUT_MS       = 1000;
        public static final int THREAD_TIMEOUT_MS       = 1000;
        public static final int NODE_TIMEOUT_SEC        = 5;
        public static final int MESSAGE_ACK_TIMEOUT_SEC = 5;
        public static final int HEARTBEAT_INTERVAL_SEC  = 5;

        public static final boolean PRINT_LOGS            = true;
        public static final boolean DEFAULT_NEW_LINE_LOGS = true;
        public static final boolean CLEAR_PREVIOUS_LOGS   = true;
        public static final boolean ALLOW_CUSTOM_IPS      = true;

        public static final InetAddress IP_ADDRESS   = CompilingFunctions.getIpAddress();
        public static final InetAddress BROADCAST_IP = CompilingFunctions.getBroadcastIp();

        public static final String HASHING_ALGORITHM = "SHA-256";

        public static final long MAX_FILE_SIZE = 1 << 27; // 128 MB

        public static final class Paths {
            public static final String SEND_FOLDER_PATH    = "./.SEND/";
            public static final String RECEIVE_FOLDER_PATH = "./.RECEIVE/";
            public static final String LOG_FOLDER_PATH     = "./logs/";

            public static final String PRETTY_LOGS_FILE    = "pretty.log";
            public static final String ACTUAL_LOGS_FILE    = "regular.log";
            public static final String INTERNAL_LOGS_FILE  = "internal.log";
            public static final String RECEIVED_LOGS_FILE  = "received.log";
            public static final String SENT_LOGS_FILE      = "sent.log";
            public static final String TALK_LOGS_FILE      = "talk.log";
            
            private Paths() {
                throw new IllegalStateException("Utility class");
            }
        }

        private Configs() {
            throw new IllegalStateException("Utility class");
        }
    }
        
    public static final class Strings {
        public static final String DATE_TIME_LOG_FORMAT   = "uuuu-MM-dd HH:mm:ss.SSS";
        public static final String SPACED_MESSAGE_FORMAT  = "\t\t\t\t\t%s: %s";
        public static final String REGULAR_MESSAGE_FORMAT = "%s: %s";

        public static final String EXIT_MESSAGE      = "EXIT";
        public static final String FULL_FILE_REQUEST = "SEND_FULL_FILE";

        public static final String HEARTBEAT_FORMAT = "HEARTBEAT %s";
        public static final String TALK_FORMAT      = "TALK %d %s";
        public static final String FILE_FORMAT      = "FILE %d %s %d";
        public static final String CHUNK_FORMAT     = "CHUNK %d %d %s";
        public static final String END_FORMAT       = "END %d %s";

        // Id-less formats
        public static final String SIMPLE_TALK_FORMAT  = "TALK %s";
        public static final String SIMPLE_FILE_FORMAT  = "FILE %s %d";
        public static final String SIMPLE_CHUNK_FORMAT = "CHUNK %d %s";
        public static final String SIMPLE_END_FORMAT   = "END %s";
        public static final String ACK_FORMAT          = "ACK %s";
        public static final String NACK_FORMAT         = "NACK %d %s";
        public static final String UNSUPPORTED_FORMAT  = "UNSUPPORTED: %s";
        public static final String RESEND_FORMAT       = "RESEND %d";

        public static final String HEARTBEAT_LOG_FORMAT   = "(%s) [%s] HEARTBEAT";
        public static final String TALK_LOG_FORMAT        = "(%s) [%s] TALK(%d) : \"%s\"";
        public static final String FILE_LOG_FORMAT        = "(%s) [%s] FILE(%d) : \"%s\" (%d bytes)";
        public static final String CHUNK_LOG_FORMAT       = "(%s) [%s] CHUNK(%d): (seq %d) %s (%d bytes)";
        public static final String END_LOG_FORMAT         = "(%s) [%s] END(%d)  : \"%s\"";
        public static final String ACK_LOG_FORMAT         = "(%s) [%s] ACK   : Acknowleged '%d'";
        public static final String NACK_LOG_FORMAT        = "(%s) [%s] NACK  : Didn't acknowlege '%d' because \"%s\"";
        public static final String UNSUPPORTED_LOG_FORMAT = "(%s) [%s] Unsupported message: %s";

        public static final String EXIT_REQUEST_LOG_FORMAT   = "(%s) EXIT";
        public static final String TALK_SENDING_LOG_FORMAT   = "(%s) TALK_SENDING: %s";
        public static final String FILE_SENDING_LOG_FORMAT   = "(%s) FILE_SENDING: %s";
        public static final String CHUNK_SENDING_LOG_FORMAT  = "(%s) CHUNK_SENDING: %d (%d bytes)";
        public static final String END_SENDING_LOG_FORMAT    = "(%s) END_SENDING: %s";
        public static final String ACK_SENDING_LOG_FORMAT    = "(%s) ACK_SENDING : %d";
        public static final String NACK_SENDING_LOG_FORMAT   = "(%s) NACK_SENDING: %d \"%s\"";
        public static final String RESEND_REQUEST_LOG_FORMAT = "(%s) Message %d timed out, resending...";
        public static final String DISCARTED_CHUNK_FORMAT    = "(%s) DISCARTED CHUNK: %d (%d bytes) because %s";
        public static final String FULL_FILE_REQ_LOG_FORMAT  = "(%s) FULL_FILE_REQUEST: %s";

        public static final String IP_PORT_FORMAT = "%s:%d";

        public static final String IP_ADDRESS_REGEX  = "^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$";

        private Strings() {
            throw new IllegalStateException("Utility class");
        }

        public static final class Errors {
            public static final String COMPLETE_ASSEMBLER    = "File is already complete";
            public static final String EMPTY_PACKET          = "Empty packet";
            public static final String FILE_TOO_LARGE        = "File size exceeded";
            public static final String FILE_ALREADY_COMPLETE = "File is already marked as complete";
            public static final String DIFFERENT_FILE_SIZES  = "File size is different than expected";
            public static final String DIFFERENT_HASHES      = "Hashes are different";

            private Errors() {
                throw new IllegalStateException("Utility class");
            }
        }
    }

    public static final class MessageHeaders {
        public static final Charset ENCODING = StandardCharsets.UTF_16BE;

        public static final char HEARTBEAT_HEADER = '♥'; // Alt + 3
        public static final char TALK_HEADER      = '¶'; // Alt + 0182
        public static final char FILE_HEADER      = '├'; // Alt + 195
        public static final char CHUNK_HEADER     = '─'; // Alt + 196
        public static final char END_HEADER       = '┤'; // Alt + 180
        public static final char ACK_HEADER       = '=';
        public static final char NACK_HEADER      = '¬'; // Alt + 170

        private MessageHeaders() {
            throw new IllegalStateException("Utility class");
        }
    }

    public static final class ForbiddenFileNames {
        public static final String NO_FORBIDDEN_CHARS_REGEX = "^[^<>:\"\\\\/|?*\\n]*$";
        public static final String FILE_EXTENSION_REGEX = "\\.\\w+$";

        public static final Set<String> RESERVED_NAMES = Set.of(
            "CON", "PRN", "AUX", "NUL",
            "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
            "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
        );

        private ForbiddenFileNames() {
            throw new IllegalStateException("Utility class");
        }
    }

    private static final class CompilingFunctions {
        private static InetAddress getIpAddress() {
            try {
                return InetAddress.getLocalHost();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
            return null;
        }

        private static InetAddress getBroadcastIp() {
            try {
                return InetAddress.getByName("255.255.255.255");
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
            return null;
        }

        @SuppressWarnings("unused")
        private static int calculateMinimumMaxChunkSize() {
            final int MIN_CHAR_COUNT = (
                  Character.BYTES  // header
                + Integer.BYTES    // message Id
                + Character.BYTES  // space
                + Integer.BYTES    // chunk number
                + Character.BYTES  // space

            ); 
            
            // UTF_16BE encoding uses 2 bytes per character
            // So we need to divide the max message size by 2 to get the max chunk size
            final int RESULT = (Configs.MAX_MESSAGE_SIZE / 2) - MIN_CHAR_COUNT;
            if (RESULT < 0) {
                throw new IllegalStateException("Max message size is too small to fit a chunk header");
            }

            return RESULT;
        }

        private CompilingFunctions() {
            throw new IllegalStateException("Utility class");
        }
    }
}
