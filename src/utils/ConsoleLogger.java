package utils;

import static utils.Constants.Configs.DEFAULT_NEW_LINE_LOGS;

public class ConsoleLogger {

    private class Colours {
        private static final String RESET  = "\u001B[0m";
        private static final String BLACK  = "\u001B[30m";
        private static final String RED    = "\u001B[31m";
        private static final String GREEN  = "\u001B[32m";
        private static final String YELLOW = "\u001B[33m";
        private static final String BLUE   = "\u001B[34m";
        private static final String PURPLE = "\u001B[35m";
        private static final String CYAN   = "\u001B[36m";
        private static final String WHITE  = "\u001B[37m";

        private Colours() {
            throw new IllegalStateException("Utility class");
        }
    } 

    public static synchronized void logBlack(String message) {
        logBlack(message, DEFAULT_NEW_LINE_LOGS);
    }

    public static synchronized void logRed(String message) {
        logRed(message, DEFAULT_NEW_LINE_LOGS);
    }

    public static synchronized void logGreen(String message) {
        logGreen(message, DEFAULT_NEW_LINE_LOGS);
    }
    
    public static synchronized void logYellow(String message) {
        logYellow(message, DEFAULT_NEW_LINE_LOGS);
    }

    public static synchronized void logBlue(String message) {
        logBlue(message, DEFAULT_NEW_LINE_LOGS);
    }
    
    public static synchronized void logPurple(String message) {
        logPurple(message, DEFAULT_NEW_LINE_LOGS);
    }
    
    public static synchronized void logCyan(String message) {
        logCyan(message, DEFAULT_NEW_LINE_LOGS);
    }

    public static synchronized void logWhite(String message) {
        logWhite(message, DEFAULT_NEW_LINE_LOGS);
    }
    public static synchronized void logBlack(String message, boolean newLine) {
        System.out.print(Colours.BLACK);
        System.out.print(message);

        if (newLine) {
            System.out.println(Colours.RESET);
        } else {
            System.out.print(Colours.RESET);
        }
    }

    public static synchronized void logRed(String message, boolean newLine) {
        System.out.print(Colours.RED);
        System.out.print(message);

        if (newLine) {
            System.out.println(Colours.RESET);
        } else {
            System.out.print(Colours.RESET);
        }
    }

    public static synchronized void logGreen(String message, boolean newLine) {
        System.out.print(Colours.GREEN);
        System.out.print(message);

        if (newLine) {
            System.out.println(Colours.RESET);
        } else {
            System.out.print(Colours.RESET);
        }
    }
    
    public static synchronized void logYellow(String message, boolean newLine) {
        System.out.print(Colours.YELLOW);
        System.out.print(message);

        if (newLine) {
            System.out.println(Colours.RESET);
        } else {
            System.out.print(Colours.RESET);
        }
    }

    public static synchronized void logBlue(String message, boolean newLine) {
        System.out.print(Colours.BLUE);
        System.out.print(message);

        if (newLine) {
            System.out.println(Colours.RESET);
        } else {
            System.out.print(Colours.RESET);
        }
    }
    
    public static synchronized void logPurple(String message, boolean newLine) {
        System.out.print(Colours.PURPLE);
        System.out.print(message);

        if (newLine) {
            System.out.println(Colours.RESET);
        } else {
            System.out.print(Colours.RESET);
        }
    }
    
    public static synchronized void logCyan(String message, boolean newLine) {
        System.out.print(Colours.CYAN);
        System.out.print(message);

        if (newLine) {
            System.out.println(Colours.RESET);
        } else {
            System.out.print(Colours.RESET);
        }
    }

    public static synchronized void logWhite(String message, boolean newLine) {
        System.out.print(Colours.WHITE);
        System.out.print(message);

        if (newLine) {
            System.out.println(Colours.RESET);
        } else {
            System.out.print(Colours.RESET);
        }
    }

    public static synchronized void log(String message) {
        System.out.print(Colours.RESET);
        System.out.println(message);
    }
    
    public static synchronized void logError(String message, Exception e) {
        System.out.print(Colours.RED);
        System.err.printf("ERROR: %s%n", message);

        System.out.print(Colours.YELLOW);
        e.printStackTrace();
        System.out.print(Colours.RESET);
    }

    public static synchronized void logError(String message) {
        System.out.print(Colours.RED);
        System.err.printf("ERROR: %s", message);
        System.out.println(Colours.RESET);
    }
    
    public static synchronized void logError(Exception e) {
        System.out.print(Colours.RED);
        System.err.printf("ERROR: %s%n", e.getMessage());

        System.out.print(Colours.YELLOW);
        e.printStackTrace();
        System.out.print(Colours.RESET);
    }

    private ConsoleLogger() {
        throw new IllegalStateException("Utility class");
    }
}