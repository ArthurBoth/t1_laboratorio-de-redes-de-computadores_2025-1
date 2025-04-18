package io.fileIO;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import constants.Constants;

public abstract class FileUitls {
    public static boolean isValidFileName(String fileName) {
        String extensionlessFileName;

        if (fileName == null) return false;
        if (fileName.isEmpty()) return false;
        if (!fileName.matches(Constants.ForbiddenFileNames.NO_FORBIDDEN_CHARS_REGEX)) return false;
        if (fileName.charAt(fileName.length() - 1) == ' ') return false;
        if (fileName.charAt(fileName.length() - 1) == '.') return false;

        extensionlessFileName = fileName.split(Constants.ForbiddenFileNames.FILE_EXTENSION_REGEX)[0];

        return !(Constants.ForbiddenFileNames.RESERVED_NAMES.contains(extensionlessFileName.toUpperCase()));
    }

    public static String problemsCreatingFile(File file) {
        if (!FileUitls.isValidFileName(file.getName())) return "Invalid file name";
        if (file.exists()) return "File already exists";

        return null;
    }

    private static byte[] hashFile(byte[] fileBytes) throws NoSuchAlgorithmException {
        MessageDigest digestor = MessageDigest.getInstance(Constants.Configs.HASHING_ALGORITHM);
        return digestor.digest(fileBytes);
    } 

    private static String hashToString(byte[] hashBytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String getFileHash(byte[] data) throws NoSuchAlgorithmException {
        return hashToString(hashFile(data));
    }
}