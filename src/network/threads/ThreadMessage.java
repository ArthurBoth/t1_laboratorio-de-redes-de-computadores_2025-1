package network.threads;

public class ThreadMessage {
    public final boolean internalMessage;
    public final String message;

    private ThreadMessage(Builder builder) {
        this.internalMessage = builder.internalMessage;
        this.message         = builder.message;
    }

    public static MessageSetter internalMessage(boolean flag) {
        return new Builder(flag);
    }

    public interface MessageSetter {
        ThreadMessage message(String message);
    }

    private static class Builder implements MessageSetter {
        private boolean internalMessage;
        private String message;

        public Builder(boolean internalMessage) {
            this.internalMessage = internalMessage;
        }

        @Override
        public ThreadMessage message(String message) {
            this.message = message;
            return new ThreadMessage(this);
        }
    }
}
