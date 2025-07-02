package fr.epita.assistants.ping.utils;

import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Map;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.util.Optional;

@ApplicationScoped
public class Logger {
    private final String RESET = "\u001B[0m";
    private final String GREEN = "\u001B[32m";
    private final String RED = "\u001B[31m";
    @ConfigProperty(name = "LOG_FILE")
    private Optional<String> LOG_FILE;
    @ConfigProperty(name = "ERROR_LOG_FILE")
    private Optional<String> ERROR_LOG_FILE;
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy - HH:mm:ss");

    public void logRequest(String userId, String endpoint, String parameters) {
        String msg = String.format("User[%s] called %s with params: %s", userId != null ? userId : "anonymous",
                endpoint, parameters);
        log(msg);
    }

    public void logErrorRequest(String userId, String endpoint, String error) {
        String msg = String.format("User[%s] ERROR at %s -> %s", userId != null ? userId : "anonymous", endpoint,
                error);
        error(msg);
    }

    public void log(String message) {
        String formatted = format(GREEN, message);
        writeToFileOrStdout(LOG_FILE.orElse(""), formatted, false);
    }

    public void error(String message) {
        String formatted = format(RED, message);
        writeToFileOrStdout(ERROR_LOG_FILE.orElse(""), formatted, true);
    }

    private String format(String color, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        return String.format("%s [%s] %s %s", color, timestamp, message, RESET);
    }

    private void writeToFileOrStdout(String path, String message, boolean isError) {
        if (path.isEmpty()) {
            outputFallback(message, isError);
            return;
        }
        try (FileWriter writer = new FileWriter(path, true)) {
            writer.write(message + "\n");
        } catch (IOException e) {
            outputFallback(message, isError);
        }
    }

    private void outputFallback(String message, boolean isError) {
        if (isError)
            System.err.println(message);
        else
            System.out.println(message);
    }

    // Version LEO

    private final String RESET_TEXT = "\u001B[0m";
    private final String RED_TEXT = "\u001B[31m";
    private final String GREEN_TEXT = "\u001B[32m";

    private static String timestamp() {
        return new SimpleDateFormat("dd/MM/yy - HH:mm:ss")
                .format(Calendar.getInstance().getTime());
    }

    public void simpleLog(String message) {
        String tolog = GREEN_TEXT + " [" + timestamp() + "] " + RESET_TEXT;
        System.out.println(tolog);

        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            System.out.format("%s=%s%n",
                    envName,
                    env.get(envName));
        }
    }

    public void errorLog(String message) {
        String tolog = RED_TEXT + " [" + timestamp() + "] " + RESET_TEXT;
        System.out.println(tolog);
    }
}
