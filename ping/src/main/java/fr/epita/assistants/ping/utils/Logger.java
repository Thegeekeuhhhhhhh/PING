package fr.epita.assistants.ping.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";

    private static final String LOG_FILE = System.getenv("LOG_FILE");
    private static final String ERROR_LOG_FILE = System.getenv("ERROR_LOG_FILE");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy - HH:mm:ss");

    public static void logRequest(String userId, String endpoint, String parameters) {
        String msg = String.format("User[%s] called %s with params: %s", userId != null ? userId : "anonymous",
                endpoint, parameters);
        log(msg);
    }

    public static void logErrorRequest(String userId, String endpoint, String error) {
        String msg = String.format("User[%s] ERROR at %s -> %s", userId != null ? userId : "anonymous", endpoint,
                error);
        error(msg);
    }

    public static void log(String message) {
        String formatted = format(GREEN, message);
        writeToFileOrStdout(LOG_FILE, formatted, false);
    }

    public static void error(String message) {
        String formatted = format(RED, message);
        writeToFileOrStdout(ERROR_LOG_FILE, formatted, true);
    }

    private static String format(String color, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        return String.format("%s [%s] %s %s", color, timestamp, message, RESET);
    }

    private static void writeToFileOrStdout(String path, String message, boolean isError) {
        if (path == null) {
            outputFallback(message, isError);
            return;
        }
        try (FileWriter writer = new FileWriter(path, true)) {
            writer.write(message + "\n");
        } catch (IOException e) {
            outputFallback(message, isError);
        }
    }

    private static void outputFallback(String message, boolean isError) {
        if (isError)
            System.err.println(message);
        else
            System.out.println(message);
    }
}
