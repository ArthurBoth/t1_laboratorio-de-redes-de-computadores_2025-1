package constants;

public interface ConfigConstants {
    static final String LOG_FILE_NAME        = "messages.log";
    static final String DATE_TIME_LOG_FORMAT = "uuuu-MM-dd HH:mm:ss.SSS";

    static final int DEFAULT_PORT      = 9000;
    static final int MAX_MESSAGE_SIZE  = 1 << 20;
    static final int SOCKET_TIMEOUT_MS = 1000;

    static final boolean PRINT_LOGS            = true;
    static final boolean DEFAULT_NEW_LINE_LOGS = true;
    static final boolean CLEAR_PREVIOUS_LOGS   = true;
}
