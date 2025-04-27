package io.files;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import utils.ConsoleLogger;

public abstract class FileIo {
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

    public static void writeChunk(Path path, byte[] data) {
        try (
            OutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(path.toFile(), true)
            )
        ) {
            outputStream.write(data);
        } catch (IOException e) {
            ConsoleLogger.logError("An error occurred. (writing file)", e);
        }
    }

    public static byte[] readChunk(Path path, int offset, int length) {
        byte[] buffer;
        byte[] data;
        int bytesRead;

        try (InputStream inputStream = Files.newInputStream(path)) {
            inputStream.skip(offset);
            buffer    = new byte[length];
            bytesRead = inputStream.read(buffer, offset, length);

            if (bytesRead < length) {
                data = new byte[bytesRead];
                System.arraycopy(buffer, 0, data, 0, bytesRead);
                return data;
            }
            return buffer;
        } catch (IOException e) {
            ConsoleLogger.logError("An error occurred. (reading file)", e);
            return null;
        }
    }
}