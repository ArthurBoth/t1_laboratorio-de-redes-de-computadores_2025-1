package network.messages;

public interface ThreadMessageVisitor {
    // boolean to indicate wether the program should keep running
    boolean process(InternalMessage message);
    boolean process(ExternalMessage message);
    boolean process(ReceivedMessage message);
}
