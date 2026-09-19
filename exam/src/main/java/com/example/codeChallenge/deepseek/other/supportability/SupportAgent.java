package com.example.codeChallenge.deepseek.other.supportability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/*

Supportability
(قابلیت پشتیبانی) توانایی یک سیستم در ارائه‌ی ابزارها، رابط‌ها و مکانیزم‌های داخلی به تیم‌های عملیاتی (Ops) و پشتیبانی (Support) است تا بتوانند:
 */

public class SupportAgent {
    private final TransactionCounter counter;
    private final Path signalFile = Path.of("signal.status");
    private final Path stopFile = Path.of("signal.stop");
    private final Path statusFile = Path.of("status.txt");
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final long startTime = System.currentTimeMillis();
    private volatile boolean running = true;

    public SupportAgent(TransactionCounter counter, int checkIntervalSeconds) {
        this.counter = counter;
        scheduler.scheduleAtFixedRate(this::checkSignals, 0, checkIntervalSeconds, TimeUnit.SECONDS);
    }

    private void checkSignals() {
        if (!running) return;
        try {
            if (Files.exists(stopFile)) {
                stop();
                return;
            }
            if (Files.exists(signalFile)) {
                writeStatus();
                Files.delete(signalFile);
            }
        } catch (IOException e) {
            System.err.println("Signal check failed: " + e.getMessage());
        }
    }

    private void writeStatus() throws IOException {
        long uptime = (System.currentTimeMillis() - startTime) / 1000;
        long total = counter.getCount();
        long memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        String report = String.format(
                "Uptime: %ds | TX: %d | Memory: %dKB | LogLevel: INFO%n",
                uptime, total, memory / 1024
        );
        Files.writeString(statusFile, report);
        System.out.println("Status written.");
    }

    public void start() {
        System.out.println("SupportAgent started. Use 'signal.status' or 'signal.stop'.");
    }

    public void stop() {
        if (!running) return;
        running = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException ignored) {}
        System.out.println("Shutting down...");
    }
}