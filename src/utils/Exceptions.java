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

    public static class ThreadNotStartedException extends RuntimeException {
        public ThreadNotStartedException(String message) {
            super(message);
        }
    }

    public static class EndExecutionException extends RuntimeException {
        public EndExecutionException() {
            super();
        }
    }
}
