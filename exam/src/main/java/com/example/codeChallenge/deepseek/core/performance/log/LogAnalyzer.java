package com.example.codeChallenge.deepseek.core.performance.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class LogAnalyzer {
    private static final String LOG_FILE = "app.log";
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public LogAnalyzer() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void shutdown() {
        try {
            executorService.shutdown();
            boolean terminated = executorService.awaitTermination(10, TimeUnit.SECONDS);
            if (!terminated) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("InterruptedException error :" + e.getMessage());
        }
    }

    public void analyzeLog() throws IOException {
        List<CompletableFuture<LogEntry>> completableFutures = getCompletableFutures();

        CompletableFuture
                .allOf(completableFutures.toArray(new CompletableFuture[0]))
                .join();

        List<LogEntry> logEntries = new ArrayList<>();
        completableFutures.forEach(completableFuture -> {
            try {
                LogEntry entry = completableFuture.get();
                logEntries.add(entry);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("InterruptedException error:" + e.getMessage());
            } catch (ExecutionException e) {
                System.err.println("Execution error :" + e.getMessage());
            }
        });

        long totalRequests = logEntries.size();
        double sumDuration = 0;
        long failedRequests = 0;
        Set<String> logEntrySet = new HashSet<>();
        for (LogEntry logEntry : logEntries) {
            if ("FAILED".equals(logEntry.status)) {
                failedRequests++;
            }
            if ("LOGIN".equals(logEntry.action)) {
                logEntrySet.add(logEntry.userId);
            }
            sumDuration += logEntry.duration;
        }
        double avgDuration = totalRequests > 0 ? sumDuration / totalRequests : 0;

        // ۵. گزارش نهایی
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Failed Requests: " + failedRequests);
        System.out.println("Average Duration: " + avgDuration);
        System.out.println("Active Users: " + logEntrySet.size());
    }

    private List<CompletableFuture<LogEntry>> getCompletableFutures() throws IOException {
        List<CompletableFuture<LogEntry>> completableFutures = new ArrayList<>();

        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(LOG_FILE))) {

            // ۱. خواندن کل فایل
            bufferedReader.lines().forEach(line -> {
                CompletableFuture<LogEntry> completableFuture = CompletableFuture.supplyAsync(() -> {
                    LogEntry entry = parseLine(line);
                    return entry;
                }, executorService);

                completableFutures.add(completableFuture);
            });

        } catch (IOException ioException) {
            System.err.println("Error in method readFileViaBuffer." + ioException.getMessage());
            throw ioException;
        }
        return completableFutures;
    }


    private LogEntry parseLine(String line) {
        // پردازش سنگین (شبیه‌سازی)
        String[] parts = line.split("\\|");
        LogEntry entry = new LogEntry();
        entry.timestamp = parts[0];
        entry.userId = parts[1];
        entry.action = parts[2];
        entry.duration = Long.parseLong(parts[3]);
        entry.status = parts[4];
        return entry;
    }

    static class LogEntry {
        String timestamp;
        String userId;
        String action;
        long duration;
        String status;
    }
}