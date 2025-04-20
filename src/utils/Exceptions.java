package utils;

public final class Exceptions {
    public static class FileSearchException extends Exception {
        public FileSearchException(String message) {
            super(message);
        }

        public FileSearchException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class InvalidConfigurationException extends RuntimeException {
        public InvalidConfigurationException(String message) {
            super(message);
        }
    }
}
