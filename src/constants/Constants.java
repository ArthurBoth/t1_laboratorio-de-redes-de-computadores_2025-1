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

        private static String ipAddress;

        public static String getIpAddress() {
            if (ipAddress == null) {
                try {
                    ipAddress = InetAddress.getLocalHost().getHostAddress();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return String.format("%s:%d", ipAddress, DEFAULT_PORT);
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
        public static final String HEARTBEAT_MESSAGE = String.format("HEARTBEAT %s", Configs.getIpAddress());

        public static final String TALK_FORMAT  = "TALK %d %s";
        public static final String FILE_FORMAT  = "FILE %d %s %d";
        public static final String CHUNK_FORMAT = "CHUNK %d %d";
        public static final String END_FORMAT   = "END %d %s";
        public static final String ACK_FORMAT   = "ACK %d";
        public static final String NACK_FORMAT  = "NACK %d %s";

        private Strings() {
            throw new IllegalStateException("Utility class");
        }
    }
}
