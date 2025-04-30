package io.files;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import interfaces.Loggable;
import utils.Constants;
import utils.Constants.Configs.Paths;

public class FileLogger {
    private final DateTimeFormatter DATE_TIME_FORMATTER;

    public FileLogger() {
        DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.Strings.DATE_TIME_LOG_FORMAT);
        
        if (Constants.Configs.CLEAR_PREVIOUS_LOGS) {
            clearLogs();
        } else {
            logDividers();
        }
    } 

    private void logDividers() {
        File folder = new File(Paths.LOG_FOLDER_PATH);
        
        if (!folder.exists()) folder.mkdirs();
        
        FileIo.writeLine(Paths.LOG_FOLDER_PATH + Paths.PRETTY_LOGS_FILE, "*--".repeat(30) + '*');
        FileIo.writeLine(Paths.LOG_FOLDER_PATH + Paths.ACTUAL_LOGS_FILE, "*--".repeat(30) + '*');
        FileIo.writeLine(Paths.LOG_FOLDER_PATH + Paths.INTERNAL_LOGS_FILE, "*--".repeat(30) + '*');
        FileIo.writeLine(Paths.LOG_FOLDER_PATH + Paths.RECEIVED_LOGS_FILE, "*--".repeat(30) + '*');
        FileIo.writeLine(Paths.LOG_FOLDER_PATH + Paths.SENT_LOGS_FILE, "*--".repeat(30) + '*');
        FileIo.writeLine(Paths.LOG_FOLDER_PATH + Paths.TALK_LOGS_FILE, "*--".repeat(30) + '*');
    }

    private void clearLogs() {
        File folder = new File(Paths.LOG_FOLDER_PATH);
        
        if (!folder.exists()) folder.mkdirs();

        FileIo.clearFile(Paths.LOG_FOLDER_PATH + Paths.PRETTY_LOGS_FILE);
        FileIo.clearFile(Paths.LOG_FOLDER_PATH + Paths.ACTUAL_LOGS_FILE);
        FileIo.clearFile(Paths.LOG_FOLDER_PATH + Paths.INTERNAL_LOGS_FILE);
        FileIo.clearFile(Paths.LOG_FOLDER_PATH + Paths.RECEIVED_LOGS_FILE);
        FileIo.clearFile(Paths.LOG_FOLDER_PATH + Paths.SENT_LOGS_FILE);
        FileIo.clearFile(Paths.LOG_FOLDER_PATH + Paths.TALK_LOGS_FILE);
    }

    public void logSpaced(String message, String filePath) {
        String line = String.format(Constants.Strings.SPACED_MESSAGE_FORMAT, 
                                    DATE_TIME_FORMATTER.format(LocalDateTime.now()), 
                                    message);
        FileIo.writeLine(filePath, line);
    }
    
    private void log(String message, String filePath) {
        String line = String.format(Constants.Strings.REGULAR_MESSAGE_FORMAT, 
                                    DATE_TIME_FORMATTER.format(LocalDateTime.now()), 
                                    message);
        FileIo.writeLine(filePath, line);
    }

    // ****************************************************************************************************
    // Logging messages

    public void logInternal(String message) {
        log(message, Paths.LOG_FOLDER_PATH + Paths.INTERNAL_LOGS_FILE);
    }

    public void logInternal(Loggable message) {
        log(message.getPrettyMessage(), Paths.LOG_FOLDER_PATH + Paths.INTERNAL_LOGS_FILE);
        log(message.getPrettyMessage(), Paths.LOG_FOLDER_PATH + Paths.PRETTY_LOGS_FILE);
        log(message.getMessage(), Paths.LOG_FOLDER_PATH + Paths.ACTUAL_LOGS_FILE);
    }

    public void logSent(Loggable message) {
        log(message.getPrettyMessage(), Paths.LOG_FOLDER_PATH + Paths.SENT_LOGS_FILE);
        log("SENT: " + message.getPrettyMessage(), Paths.LOG_FOLDER_PATH + Paths.PRETTY_LOGS_FILE);
        log("SENT: " + message.getMessage(), Paths.LOG_FOLDER_PATH + Paths.ACTUAL_LOGS_FILE);
    }

    public void logSentTalk(Loggable message) {
        logSent(message);
        log("SENT: " + message.getPrettyMessage(), Paths.LOG_FOLDER_PATH + Paths.TALK_LOGS_FILE);
    }

    public void logReceived(Loggable message) {
        log(message.getPrettyMessage(), Paths.LOG_FOLDER_PATH + Paths.RECEIVED_LOGS_FILE);
        logSpaced("RECEIVED: " + message.getPrettyMessage(), Paths.LOG_FOLDER_PATH + Paths.PRETTY_LOGS_FILE);
        logSpaced("RECEIVED: " + message.getMessage(), Paths.LOG_FOLDER_PATH + Paths.ACTUAL_LOGS_FILE);
    }

    public void logReceivedTalk(Loggable message) {
        logReceived(message);
        logSpaced("RECEIVED: " + message.getPrettyMessage(), Paths.LOG_FOLDER_PATH + Paths.TALK_LOGS_FILE);
    }
}
