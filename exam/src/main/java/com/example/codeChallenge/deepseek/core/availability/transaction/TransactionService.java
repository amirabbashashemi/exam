package com.example.codeChallenge.deepseek.core.availability.transaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TransactionService {
    private static final String BACKUP_FILE_NAME = "backup.txt";
    private final BlockingQueue<Transaction> queue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Transaction> deadLetterQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final ScheduledExecutorService backupExecutor = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean running = true;

    public TransactionService() {
        backupExecutor.scheduleWithFixedDelay(this::backup, 0, 1, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void backup() {
        try {
            List<Transaction> transactions = new ArrayList<>(queue);
            transactions.addAll(new ArrayList<>(deadLetterQueue));

            List<String> transactionStrings = transactions.stream()
                    .map(Transaction::toString)
                    .toList();

            Path path = Path.of(BACKUP_FILE_NAME);
            if (!transactions.isEmpty()) {
                Files.write(path,
                        transactionStrings,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
            }
        } catch (IOException e) {
            System.err.println("Error in write to file");
            Thread.currentThread().interrupt();
        }
    }

    public void start() {
        executor.submit(() -> {
            while (running) {
                Transaction tx = null;
                try {
                    tx = queue.poll(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    System.err.println("Error in poll from queue");
                    Thread.currentThread().interrupt();
                }
                if (tx != null) {
                    processTransaction(tx);
                }
            }
        });
    }

    public void addTransaction(Transaction tx) {
        queue.add(tx);
    }

    private void processTransaction(Transaction tx) {
        CompletableFuture<Void> completableFuture = CompletableFuture
                // ۱. اعتبارسنجی
                .supplyAsync(() -> {
                    validate(tx);
                    return tx;
                })
                // ۲. ذخیره در دیتابیس (شبیه‌سازی)
                .thenAccept(this::saveToDatabase)
                // ۳. ارسال به سیستم خارجی (شبیه‌سازی)
                .thenRun(() -> send(tx));

        CompletableFuture
                .allOf(completableFuture)
                .join();
    }

    public void shutdown() {
        try {
            running = false;
            backup();

            executor.shutdown();
            boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);

            if (!terminated) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("Error in shutdown.");
            Thread.currentThread().interrupt();
        }
    }

    private void validate(Transaction tx) {

    }

    private void saveToDatabase(Transaction tx) {

    }

    private void send(Transaction tx) {
        try {
            sendToExternalSystem(tx);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Error in sendToExternalSystem tx:%s. try count is %d ", tx.getId(), 1);
            System.err.println(errorMessage);
            retrySend(tx, 3);
        }
    }

    private void retrySend(Transaction tx, int attempt) {
        if (attempt > 3) {
            deadLetterQueue.offer(tx);
            return;
        } else {
            try {
                scheduledExecutorService.schedule(() -> sendToExternalSystem(tx), 1, TimeUnit.SECONDS);
            } catch (Exception e) {
                retrySend(tx, ++attempt);
            }
        }
    }

    private void sendToExternalSystem(Transaction tx) {
        // ۳ ارسال به سیستم خارجی (شبیه‌سازی)
    }
}
