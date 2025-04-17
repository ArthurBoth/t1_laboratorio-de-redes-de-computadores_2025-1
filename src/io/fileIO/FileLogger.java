package io.fileIO;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import constants.Constants;
import constants.Constants.Configs.Paths;

public class FileLogger {
    private final DateTimeFormatter DATE_TIME_FORMATTER;

    public FileLogger() {
        DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.Strings.DATE_TIME_LOG_FORMAT);
        
        if (Constants.Configs.CLEAR_PREVIOUS_LOGS) clearLogs();
  
        FileIO.writeLine(Paths.LOG_FOLDER_PATH + Paths.ALL_LOGS_FILE, "*--".repeat(30) + '*');
    } 

    private void clearLogs() {
        File folder = new File(Paths.LOG_FOLDER_PATH);
        
        if (!folder.exists()) folder.mkdirs();

        FileIO.clearFile(Paths.LOG_FOLDER_PATH + Paths.ALL_LOGS_FILE);
    }

    public void logReceived(String message) {
        String line = String.format(Constants.Strings.RECEIVED_MESSAGE_FORMAT, 
                                    DATE_TIME_FORMATTER.format(LocalDateTime.now()), 
                                    message);
        FileIO.writeLine(Paths.LOG_FOLDER_PATH + Paths.ALL_LOGS_FILE, line);
    }
    
    public void logSent(String message) {
        String line = String.format(Constants.Strings.SENT_MESSAGE_FORMAT, 
                                    DATE_TIME_FORMATTER.format(LocalDateTime.now()), 
                                    message);
        FileIO.writeLine(Paths.LOG_FOLDER_PATH + Paths.ALL_LOGS_FILE, line);
    }
}
