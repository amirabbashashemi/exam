package com.example.codeChallenge.deepseek.other.accessibility;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class AccessibleLogger {
    private LogLevel logLevel;
    private final Path logFilePath;
    private static final String LOGGER_PATTERN = "[%s] # [message: %s] # [args:%s]";
    private static final String REPORT_PATTERN = "[Total: %d] [SUCCESSFUL: %d] [UNSUCCESSFUL:%d] [SUCCESSFUL PERCENT:%f]";

    public AccessibleLogger(String logFileUri, LogLevel initialLevel) {
        this.logFilePath = Path.of(logFileUri);
        this.logLevel = initialLevel;
    }

    public void debug(String message, Object... args) {
        if (logLevel.index < LogLevel.DEBUG.index) {
            return;
        }

        String argsMessage = getArgs(args);

        String logMessage = String.format(LOGGER_PATTERN, LogLevel.DEBUG, message, argsMessage);

        logToConsole(logMessage);
        logToFile(logMessage);
    }

    public void info(String message, Object... args) {
        if (logLevel.index < LogLevel.INFO.index) {
            return;
        }

        String argsMessage = getArgs(args);

        String logMessage = String.format(LOGGER_PATTERN, LogLevel.INFO, message, argsMessage);

        logToConsole(logMessage);
        logToFile(logMessage);
    }

    public void error(String message, Object... args) {
        String argsMessage = getArgs(args);

        String logMessage = String.format(LOGGER_PATTERN, LogLevel.ERROR, message, argsMessage);

        logToConsole(logMessage);
        logToFile(logMessage);
    }

    private static String getArgs(Object[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object arg : args) {
            stringBuilder
                    .append(arg)
                    .append(" ");
        }
        return stringBuilder.toString();
    }

    private void logToConsole(String message) {
        System.out.println(message);
    }

    private void logToFile(String logMessage) {
        try {
            Files.writeString(logFilePath,
                    logMessage,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("IOException in method logToFile", e);
        }
    }

    // متد برای تغییر سطح لاگ در حین اجرا
    public void setLogLevel(LogLevel newLevel) {
        this.logLevel = newLevel;
    }

    // متد برای تولید گزارش آماری (تعداد خطاها، موفقیت‌ها، میانگین زمان)
    public String generateReport() {
        int total = 0;
        int failed = 0;
        int success = 0;

        try (BufferedReader bufferedReader = Files.newBufferedReader(logFilePath)) {
            String line = bufferedReader.readLine();
            while (line != null) {
                String[] split = line.split("#");
                String level = split[0].trim();

                if (level.equals(LogLevel.ERROR.name())) {
                    total++;
                    failed++;
                } else if (line.contains("TRX:")) {
                    total++;
                    success++;
                }

                line = bufferedReader.readLine();
            }

            double percentage = 0;
            if (total != 0) {
                percentage = (double) success / total * 100;
            }

            return String.format(REPORT_PATTERN, total, success, failed, percentage);
        } catch (IOException e) {
            throw new RuntimeException("IOException in method generateReport", e);
        }
    }
}
