package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.resilience.second;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoggerServiceChatGPT {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();
    private static final BlockingQueue<String> LOG_QUEUE = new LinkedBlockingQueue<>();
    private static final int BATCH_SIZE = 50;
    private static final int FLUSH_INTERVAL_MS = 1000; // flush every 1 sec
    private static final AtomicBoolean RUNNING = new AtomicBoolean(true);

    private final Path logFile = Path.of("app.log");

    public LoggerServiceChatGPT() {
        startLoggerThread();
        addShutdownHook();
    }

    // Public method to log messages
    public void log(String message) {
        String timestampedMessage = String.format("[%s] %s%n", Instant.now(), message);
        LOG_QUEUE.add(timestampedMessage);
    }

    // Internal logger thread
    private void startLoggerThread() {
        EXECUTOR_SERVICE.submit(() -> {
            List<String> batch = new ArrayList<>(BATCH_SIZE);
            while (RUNNING.get() || !LOG_QUEUE.isEmpty()) {
                try {
                    String log = LOG_QUEUE.poll(FLUSH_INTERVAL_MS, TimeUnit.MILLISECONDS);
                    if (log != null) {
                        batch.add(log);
                    }
                    if (batch.size() >= BATCH_SIZE || (!batch.isEmpty() && log == null)) {
                        flush(batch);
                        batch.clear();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    System.err.println("Logger exception: " + e.getMessage());
                }
            }
            // Final flush before shutdown
            if (!batch.isEmpty()) {
                flush(batch);
            }
        });
    }

    // Flush batch to disk
    private void flush(List<String> batch) {
        try {
            Files.writeString(logFile,
                    String.join("", batch),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to write logs: " + e.getMessage());
        }
    }

    // Add shutdown hook for safe shutdown and backup
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            RUNNING.set(false);
            EXECUTOR_SERVICE.shutdown();
            try {
                EXECUTOR_SERVICE.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            backupLogs();
        }));
    }

    // Backup remaining logs (simple DR example)
    private void backupLogs() {
        Path backupPath = Path.of("backup-logs.log");
        List<String> remainingLogs = new ArrayList<>();
        LOG_QUEUE.drainTo(remainingLogs);
        if (!remainingLogs.isEmpty()) {
            try {
                Files.writeString(
                        backupPath,
                        String.join("", remainingLogs),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.err.println("Failed to backup logs: " + e.getMessage());
            }
        }
    }

}

