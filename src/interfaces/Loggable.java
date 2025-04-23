package interfaces;

import interfaces.visitors.LoggerVisitor;

public interface Loggable {
    String getMessage();
    String getPrettyMessage();
    
    // ****************************************************************************************************
    // Visitor pattern for custom loggers

    void accept(LoggerVisitor visitor);
}
