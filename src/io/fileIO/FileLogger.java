package io.fileIO;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import interfaces.ForeignLoggable;
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
        
        FileIO.writeLine(Paths.LOG_FOLDER_PATH + Paths.PRETTY_LOGS_FILE, "*--".repeat(30) + '*');
        FileIO.writeLine(Paths.LOG_FOLDER_PATH + Paths.ACTUAL_LOGS_FILE, "*--".repeat(30) + '*');
        FileIO.writeLine(Paths.LOG_FOLDER_PATH + Paths.INTERNAL_LOGS_FILE, "*--".repeat(30) + '*');
    }

    private void clearLogs() {
        File folder = new File(Paths.LOG_FOLDER_PATH);
        
        if (!folder.exists()) folder.mkdirs();

        FileIO.clearFile(Paths.LOG_FOLDER_PATH + Paths.PRETTY_LOGS_FILE);
        FileIO.clearFile(Paths.LOG_FOLDER_PATH + Paths.ACTUAL_LOGS_FILE);
        FileIO.clearFile(Paths.LOG_FOLDER_PATH + Paths.INTERNAL_LOGS_FILE);
    }

    public void logSpaced(String message, String filePath) {
        String line = String.format(Constants.Strings.SPACED_MESSAGE_FORMAT, 
                                    DATE_TIME_FORMATTER.format(LocalDateTime.now()), 
                                    message);
        FileIO.writeLine(filePath, line);
    }
    
    private void log(String message, String filePath) {
        String line = String.format(Constants.Strings.REGULAR_MESSAGE_FORMAT, 
                                    DATE_TIME_FORMATTER.format(LocalDateTime.now()), 
                                    message);
        FileIO.writeLine(filePath, line);
    }

    public void logInternal(String message) {
        log(message, Paths.LOG_FOLDER_PATH + Paths.INTERNAL_LOGS_FILE);
    }

    public void logInternal(Loggable message) {
        log(message.getActualMessage(), Paths.LOG_FOLDER_PATH + Paths.INTERNAL_LOGS_FILE);
    }

    public void logForeign(ForeignLoggable message) {
        logSpaced(message.getPrettyMessage(), Paths.LOG_FOLDER_PATH + Paths.PRETTY_LOGS_FILE);
        logSpaced(message.getActualMessage(), Paths.LOG_FOLDER_PATH + Paths.ACTUAL_LOGS_FILE);
    }

    public void logTalk(ForeignLoggable message) {
        logForeign(message);
        logSpaced(message.getPrettyMessage(), Paths.LOG_FOLDER_PATH + Paths.TALK_LOGS_FILE);
    }
}
