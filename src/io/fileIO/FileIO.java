package io.fileIO;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import constants.Constants;
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

    public static byte[] readFile(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            ConsoleLogger.logError("An error occurred. (reading file)", e);
        }
        return null;
    }

    public static byte[] hashFile(Path path) throws NoSuchAlgorithmException {
        MessageDigest digest;
        DigestInputStream digestInputStream;
        byte[] buffer;

        try (InputStream inputStream = Files.newInputStream(path)) {
            digest            = MessageDigest.getInstance("SHA-256");
            digestInputStream = new DigestInputStream(inputStream, digest);
            buffer            = new byte[Constants.Configs.MAX_BUFFER_SIZE];
            while (digestInputStream.read(buffer) != -1);
        } catch (IOException e) {
            ConsoleLogger.logError("An error occurred. (hashing file)", e);
            return null;
        }
        
        return digest.digest();
    }
}