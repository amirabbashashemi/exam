package com.example.codeChallenge.deepseek.other.recoverability.me;

import com.google.gson.Gson;
import codeChallenge.deepseek.other.recoverability.Transaction;
import codeChallenge.deepseek.other.recoverability.TransactionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/*
شما مسئول پیاده‌سازی یک پردازندهٔ دسته‌ای (Batch Processor) برای تایید تراکنش‌های مالی هستید. این پردازنده باید بتواند پس از هر نوع خاموشی ناگهانی (مثل OutOfMemoryError یا قطع برق) مجدداً اجرا شود و از جایی که کار را رها کرده، ادامه دهد، بدون اینکه هیچ تراکنشی دو بار پردازش شود یا از قلم بیفتد.
فرض کن متد process(Transaction t) از قبل در اختیار توست و فقط باید آن را صدا بزنی.
 */
public class RecoverableBatchProcessor {
    private static boolean running;
    private static final int MAX_QUEUE_SIZE = 100000;
    private final Gson gson = new Gson();
    private final String baseUrl;
    private final Map<String, Transaction> FAILD_MAP = new ConcurrentHashMap<>();
    private final Map<String, Transaction> SUCCESSFUL_MAP = new ConcurrentHashMap<>();
    private final ScheduledExecutorService BACKUP_EXECUTOR = new ScheduledThreadPoolExecutor(1);
    private final ExecutorService PROCESSOR_EXECUTOR = Executors.newSingleThreadExecutor();
    private final BlockingQueue<Transaction> TRANSACTION_QUEUE = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);

    public RecoverableBatchProcessor(String baseUrl) {
        recover();
        startProcessing();
        this.baseUrl = baseUrl;
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        BACKUP_EXECUTOR.scheduleAtFixedRate(this::backup, 0, 5, TimeUnit.SECONDS);
    }

    private void startProcessing() {
        running = true;

        TransactionProcessor transactionProcessor = new TransactionProcessor();

        PROCESSOR_EXECUTOR.submit(() -> {
            while (running) {
                Transaction transaction = null;
                try {
                    transaction = TRANSACTION_QUEUE.poll(1, TimeUnit.SECONDS);

                    if (Objects.isNull(transaction)) {
                        continue;
                    }

                    transactionProcessor.process(transaction);

                    SUCCESSFUL_MAP.put(transaction.id(), transaction);
                    FAILD_MAP.remove(transaction.id());
                } catch (InterruptedException e) {
                    if (transaction != null) {
                        FAILD_MAP.put(transaction.id(), transaction);
                        SUCCESSFUL_MAP.remove(transaction.id());
                    }
                    System.out.printf("InterruptedException in method startProcessing: %s", e.getMessage());
                    Thread.currentThread().interrupt();
                } catch (TransactionException e) {
                    if (transaction != null) {
                        FAILD_MAP.put(transaction.id(), transaction);
                        SUCCESSFUL_MAP.remove(transaction.id());

                    }
                    System.out.printf("TransactionException in method startProcessing: %s", e.getMessage());
                }
            }
        });
    }

    private void recover() {
        Path path = Path.of(baseUrl);

        Path failedBackupPath = path.resolve("failed.backup");
        fileToMap(failedBackupPath, FAILD_MAP);

        Path successfulBackupPath = path.resolve("successful.backup");
        fileToMap(successfulBackupPath, SUCCESSFUL_MAP);

        Path queueBackupPath = path.resolve("queue.backup.backup");
        recoverQueue(queueBackupPath);
    }

    private void fileToMap(Path path, Map<String, Transaction> map) {
        if (!Files.exists(path)) {
            return;
        }
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            bufferedReader.lines().forEach(line -> {
                Transaction transaction = gson.fromJson(line, Transaction.class);

                map.put(transaction.id(), transaction);
            });
        } catch (IOException e) {
            System.out.printf("IOException in method recoverWithPath: %s", e.getMessage());
            Thread.currentThread().interrupt();
        }

    }

    private void recoverQueue(Path path) {
        if (!Files.exists(path)) {
            return;
        }
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            bufferedReader.lines().forEach(line -> {
                Transaction transaction = gson.fromJson(line, Transaction.class);

                boolean offered = TRANSACTION_QUEUE.offer(transaction);
                if (!offered) {
                    throw new RuntimeException("Input queue is full");
                }
            });
        } catch (IOException e) {
            System.out.printf("IOException in method recoverWithPath: %s", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void backup() {
        try {
            CompletableFuture
                    .runAsync(this::backupFailed)
                    .thenRunAsync(this::backupSuccessful)
                    .thenRunAsync(this::backupQueue)
                    .join();
        } catch (Exception e) {
            System.err.printf("An exception occurred in method backup: %s", e.getMessage());
        }
    }

    private void backupFailed() {
        List<Transaction> failedTransactions = (List<Transaction>) FAILD_MAP.values();
        if (failedTransactions.isEmpty()) {
            return;
        }

        Path path = Path.of(baseUrl);
        Path failedPath = path.resolve("failed.backup");

        saveWrite(failedPath, failedTransactions);
    }

    private void backupSuccessful() {
        List<Transaction> successfulTransactions = (List<Transaction>) SUCCESSFUL_MAP.values();
        if (successfulTransactions.isEmpty()) {
            return;
        }

        Path path = Path.of(baseUrl);
        Path successPath = path.resolve("successful.backup");
        saveWrite(successPath, successfulTransactions);

    }

    private void backupQueue() {
        List<Transaction> inProgressTransactions = new ArrayList<>();
        TRANSACTION_QUEUE.drainTo(inProgressTransactions);
        if (inProgressTransactions.isEmpty()) {
            return;
        }

        Path path = Path.of(baseUrl);
        Path inProgressPath = path.resolve("queue.backup");
        saveWrite(inProgressPath, inProgressTransactions);
    }

    private void saveWrite(Path path, List<Transaction> transactions) {
        try {
            List<String> lines = transactions
                    .stream()
                    .map(gson::toJson)
                    .toList();

            if (lines.isEmpty()) {
                return;
            }

            Files.write(
                    path,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            System.err.printf("IOException in method saveWrite: %s", e.getMessage());
        }
    }


    public void processAll(List<Transaction> transactions) throws IOException {
        transactions.forEach(transaction -> {
                    boolean offered = TRANSACTION_QUEUE.offer(transaction);
                    if (!offered) {
                        throw new RuntimeException("Input queue is full");
                    }
                }
        );
    }

    private void shutdown() {
        try {
            running = false;

            BACKUP_EXECUTOR.shutdown();
            if (!BACKUP_EXECUTOR.awaitTermination(10, TimeUnit.SECONDS)) {
                BACKUP_EXECUTOR.shutdownNow();
            }

            PROCESSOR_EXECUTOR.shutdown();
            if (!PROCESSOR_EXECUTOR.awaitTermination(10, TimeUnit.SECONDS)) {
                PROCESSOR_EXECUTOR.shutdownNow();
            }

            backup();
        } catch (InterruptedException e) {
            System.out.printf("InterruptedException in method shutdown: %s", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
