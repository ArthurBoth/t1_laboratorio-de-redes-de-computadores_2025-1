package utils;

import java.io.File;

public abstract class FileUtils {
    /**
     * Checks if a file name is valid. <br></br>
     * A file name is valid if it does not contain any forbidden characters,
     * does not end with a space or a dot, and is not a reserved name. <br></br>
     * The reserved names are: <br></br>
     * - CON <br></br>
     * - PRN <br></br>
     * - AUX <br></br>
     * - NUL <br></br>
     * - COM1 <br></br>
     * - COM2 <br></br>
     * - COM3 <br></br>
     * - COM4 <br></br>
     * - COM5 <br></br>
     * - COM6 <br></br>
     * - COM7 <br></br>
     * - COM8 <br></br>
     * - COM9 <br></br>
     * - LPT1 <br></br>
     * - LPT2 <br></br>
     * - LPT3 <br></br>
     * - LPT4 <br></br>
     * - LPT5 <br></br>
     * - LPT6 <br></br>
     * - LPT7 <br></br>
     * - LPT8 <br></br>
     * - LPT9 <br></br>
     * @param fileName the file name to check
     * @return true if the file name is valid, false otherwise
     * @see Constants.ForbiddenFileNames
     */
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

    /**
     * Checks if a file can be created. <br></br>
     * A file can be created if it does not exist and its name is valid. <br></br>
     * @param file the {@code file} object to check
     * @return true if the file can be created, false otherwise
     * @see #isValidFileName(String)
     * @see Constants.ForbiddenFileNames
     */
    public static boolean canCreateFile(File file) {
        if (!FileUtils.isValidFileName(file.getName())) return false;
        if (file.exists()) return false;

        return true;
    }

    public static String byteArrayToString(byte[] data) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < data.length; i++) {
            sb.append((char) (data[i] & 0xFF));
            if (i == (data.length - 1)) sb.append(", ");
        }
        sb.append("}");
        return sb.toString();
    }
}