package network.messages.foreign;

import interfaces.visitors.AcknowledgementVisitor;

public abstract class ForeignAcknowledgement extends ForeignMessage {
    abstract void accept(AcknowledgementVisitor visitor); 
}
