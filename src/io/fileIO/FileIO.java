package io.fileIO;

import java.io.FileWriter;
import java.io.IOException;

import io.consoleIO.ConsoleLogger;

public abstract class FileIO {
    public static void writeLine(String path, String line) {
        try {
            FileWriter fileWriter = new FileWriter(path, true);

            fileWriter.write(String.format("%s%n", line));

            fileWriter.close();
            
        } catch (IOException e) {
            ConsoleLogger.logError("An error occurred. (writing line-by-line)", e);
        }
    }

    public static void clearFile(String path) {
        try {
            FileWriter fileWriter = new FileWriter(path);

            fileWriter.write("");
            
            fileWriter.close();
            
        } catch (IOException e) {
            ConsoleLogger.logError("An error occurred. (clearing file)", e);
        }
    }
}