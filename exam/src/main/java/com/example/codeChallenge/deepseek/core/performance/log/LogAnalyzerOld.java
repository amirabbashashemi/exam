package com.example.codeChallenge.deepseek.core.performance.log;

import java.io.*;
import java.nio.file.*;
import java.util.*;

//timestamp|userId|action|duration|status
//2024-01-15T10:30:00|user123|LOGIN|150|SUCCESS
public class LogAnalyzerOld {
    private static final String LOG_FILE = "app.log";

    public void analyzeLog() throws IOException {
        // ۱. خواندن کل فایل
        List<String> lines = Files.readAllLines(Paths.get(LOG_FILE));

        // ۲. پردازش خط به خط
        List<LogEntry> entries = new ArrayList<>();
        for (String line : lines) {
            LogEntry entry = parseLine(line);
            entries.add(entry);
        }

        // ۳. محاسبه آمار
        long totalRequests = entries.size();
        long failedRequests = entries.stream()
                .filter(e -> "FAILED".equals(e.status))
                .count();
        double avgDuration = entries.stream()
                .mapToLong(e -> e.duration)
                .average()
                .orElse(0);

        // ۴. پیدا کردن کاربران فعال
        Set<String> activeUsers = new HashSet<>();
        for (LogEntry entry : entries) {
            if ("LOGIN".equals(entry.action)) {
                activeUsers.add(entry.userId);
            }
        }

        // ۵. گزارش نهایی
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Failed Requests: " + failedRequests);
        System.out.println("Average Duration: " + avgDuration);
        System.out.println("Active Users: " + activeUsers.size());
    }

    private LogEntry parseLine(String line) {
        // پردازش سنگین (شبیه‌سازی)
        try { Thread.sleep(1); } catch (InterruptedException e) {}

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