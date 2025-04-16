package constants;

import java.net.InetAddress;

public final class Constants {
    public static final class Configs {
        public static final int DEFAULT_PORT       = 9000;
        public static final int MAX_MESSAGE_SIZE   = 1 << 20; // 1 MB
        public static final int SOCKET_TIMEOUT_MS  = 1000;
        public static final int THREAD_TIMEOUT_MS  = 1000;
        public static final int NODE_TIMEOUT_SEC   = 5;
        public static final int TIMEOUT_MULTIPLYER = 3;

        public static final boolean PRINT_LOGS            = true;
        public static final boolean DEFAULT_NEW_LINE_LOGS = true;
        public static final boolean CLEAR_PREVIOUS_LOGS   = true;
        public static final String Strings = null;

        public static final String IP_ADDRESS = getIpAddress();

        private static String getIpAddress() {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public static final class Logs {
            public static final String LOG_FOLDER_PATH = "./logs/";
            public static final String ALL_LOGS_FILE   = "all.log";
            
            private Logs() {
                throw new IllegalStateException("Utility class");
            }
        }

        private Configs() {
            throw new IllegalStateException("Utility class");
        }
    }
        
    public static final class Strings {
        public static final String DATE_TIME_LOG_FORMAT    = "uuuu-MM-dd HH:mm:ss.SSS";
        public static final String RECEIVED_MESSAGE_FORMAT = "\t\t\t\t\t%s: %s";
        public static final String SENT_MESSAGE_FORMAT     = "%s: %s";
        
        public static final String BROADCAST_IP      = "255.255.255.255";
        public static final String HEARTBEAT_MESSAGE = "%s%s".formatted(
                                                            MessageHeaders.HEARTBEAT_HEADER, 
                                                            Configs.IP_ADDRESS);

        public static final String TALK_FORMAT  = "%s%%d %%s".formatted(MessageHeaders.TALK_HEADER);
        public static final String FILE_FORMAT  = "%s%%d %%s %%d".formatted(MessageHeaders.FILE_HEADER);
        public static final String CHUNK_FORMAT = "%s%%d %%d".formatted(MessageHeaders.CHUNK_HEADER); // TODO add sequence number
        public static final String END_FORMAT   = "%s%%d %%s".formatted(MessageHeaders.END_HEADER);
        public static final String ACK_FORMAT   = "%s%%d".formatted(MessageHeaders.ACK_HEADER);
        public static final String NACK_FORMAT  = "%s%%d %%s".formatted(MessageHeaders.NACK_HEADER);

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
}
