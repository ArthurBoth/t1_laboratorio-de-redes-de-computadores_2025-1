package utils;

public final class Exceptions {
    public static class FileException extends Exception {
        public FileException(String message) {
            super(message);
        }

        public FileException(String message, Throwable cause) {
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
