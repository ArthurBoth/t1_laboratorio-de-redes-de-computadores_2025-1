package io.fileIO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import constants.ConfigConstants;

public class FileLogger {
    private final DateTimeFormatter dateTime;

    public FileLogger() {
        dateTime = DateTimeFormatter.ofPattern(ConfigConstants.DATE_TIME_LOG_FORMAT);
        
        if (ConfigConstants.CLEAR_PREVIOUS_LOGS) 
            FileIO.clearFile(ConfigConstants.LOG_FILE_NAME);
  
        FileIO.writeLine(ConfigConstants.LOG_FILE_NAME, "*--".repeat(30) + '*');
    } 

    public void log(String message) {
        String line = String.format("%s: %s", dateTime.format(LocalDateTime.now()), message);
        FileIO.writeLine(ConfigConstants.LOG_FILE_NAME, line);
    }
}
