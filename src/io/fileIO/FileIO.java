package io.fileIO;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import io.consoleIO.ConsoleLogger;
import utils.FileUtils;

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

    public static byte[] readFile(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            ConsoleLogger.logError("An error occurred. (reading file)", e);
        }
        return null;
    }

    public static void writeFile(String fileName, byte[] data) {
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileName))) {
            outputStream.write(data);
        } catch (IOException e) {
            ConsoleLogger.logError("An error occurred. (writing file)", e);
        }
    }

    public static String hashFile(Path path) throws NoSuchAlgorithmException {
        byte[] fileBytes = readFile(path);
        if (fileBytes == null) return null;
        
        return FileUtils.getFileHash(fileBytes);
    }
}