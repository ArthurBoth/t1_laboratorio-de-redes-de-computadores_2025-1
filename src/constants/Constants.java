package constants;

import java.net.InetAddress;
import java.util.Set;

public final class Constants {
    public static final class Configs {
        public static final int DEFAULT_PORT       = 9000;
        public static final int MAX_MESSAGE_SIZE   = 1 << 17;  // 128 KB
        public static final int MAX_BUFFER_SIZE    = 1 << 13;  // 8 KB
        public static final int SOCKET_TIMEOUT_MS  = 1000;
        public static final int THREAD_TIMEOUT_MS  = 1000;
        public static final int NODE_TIMEOUT_SEC   = 5;
        public static final int TIMEOUT_MULTIPLYER = 3;

        public static final int MIN_CHUNK_SIZE = (
            Strings.CHUNK_FORMAT.formatted(Integer.MAX_VALUE, MAX_MESSAGE_SIZE).getBytes().length
        );

        public static final boolean PRINT_LOGS            = true;
        public static final boolean DEFAULT_NEW_LINE_LOGS = true;
        public static final boolean CLEAR_PREVIOUS_LOGS   = true;

        public static final String IP_ADDRESS = getIpAddress();
        public static final String HASHING_ALGORITHM = "SHA-256";

        public static final long MAX_FILE_SIZE = 1 << 27; // 128 MB
        
        private static String getIpAddress() {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public static final class Paths {
            public static final String SEND_FOLDER_PATH    = "./.SEND/";
            public static final String RECEIVE_FOLDER_PATH = "./.RECEIVE/";
            public static final String LOG_FOLDER_PATH     = "./logs/";
            public static final String PRETTY_LOGS_FILE    = "pretty.log";
            public static final String ACTUAL_LOGS_FILE    = "regular.log";
            public static final String INTERNAL_LOGS_FILE  = "internal.log";
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
        
        public static final String BROADCAST_IP      = "255.255.255.255";
        public static final String HEARTBEAT_MESSAGE = "%s%s".formatted(
                                                            MessageHeaders.HEARTBEAT_HEADER, 
                                                            Configs.IP_ADDRESS);

        public static final String TALK_FORMAT  = "%s%%d %%s".formatted(MessageHeaders.TALK_HEADER);
        public static final String FILE_FORMAT  = "%s%%d %%s %%d".formatted(MessageHeaders.FILE_HEADER);
        public static final String CHUNK_FORMAT = "%s%%d %%d ".formatted(MessageHeaders.CHUNK_HEADER);
        public static final String END_FORMAT   = "%s%%d %%s".formatted(MessageHeaders.END_HEADER);
        public static final String ACK_FORMAT   = "%s%%d".formatted(MessageHeaders.ACK_HEADER);
        public static final String NACK_FORMAT  = "%s%%d %%s".formatted(MessageHeaders.NACK_HEADER);

        public static final String HEARTBEAT_LOG_FORMAT = "[%s] HEARTBEAT";
        public static final String TALK_LOG_FORMAT      = "[%s] TALK(%d) : \"%s\"";
        public static final String FILE_LOG_FORMAT      = "[%s] FILE(%d) : \"%s\" (%d bytes)";
        public static final String CHUNK_LOG_FORMAT     = "[%s] CHUNK(%d): (seq %d) {%s} (%d bytes)";
        public static final String END_LOG_FORMAT       = "[%s] END(%d)  : \"%s\"";
        public static final String ACK_LOG_FORMAT       = "[%s] ACK      : AckId  = %d";
        public static final String NACK_LOG_FORMAT      = "[%s] NACK     : NAckId = %d | \"%s\"";

        public static final String DISCARTED_CHUNK_FORMAT = "DISCARTED CHUNK: %d (%d bytes)";

        private Strings() {
            throw new IllegalStateException("Utility class");
        }
    }

    public static final class MessageHeaders {
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
}
