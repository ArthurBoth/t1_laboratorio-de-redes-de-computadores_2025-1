package messages.internal.received;

import interfaces.visitors.FileMessageVisitor;

public abstract class InternalReceivedFileRelated extends InternalReceivedIdMessage {

    // ****************************************************************************************************
    // Abstract Visitor pattern for FileMessageVisitor

    public abstract void accept(FileMessageVisitor visitor);
}
