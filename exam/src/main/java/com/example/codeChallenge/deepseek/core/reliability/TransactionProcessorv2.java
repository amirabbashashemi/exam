package com.example.codeChallenge.deepseek.core.reliability;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

public class TransactionProcessorv2 {
    private static final Logger log = LoggerFactory.getLogger(TransactionProcessorv2.class);
    private static int BACKUP_INTERVAL = 1000;
    private static int RETRY_INTERVAL = 10000;
    private static int PROCESS_QUEUE_SIZE = 10000;
    private static String WAL_BACKUP_URL = "/walBackup";
    private static String FAILD_BACKUP_URL = "/failedBackup";
    private static String SUCCESSFUL_BACKUP_URL = "/successfulBackup";

    private final Gson gson = new Gson();
    private final BlockingQueue<Transaction> queue = new ArrayBlockingQueue<>(PROCESS_QUEUE_SIZE);
    private final Map<String, Transaction> walMap = new ConcurrentHashMap<>();
    private final Map<String, Transaction> failedMap = new ConcurrentHashMap<>();
    private final Map<String, Transaction> successfulMap = new ConcurrentHashMap<>();

    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    private final ScheduledExecutorService failedExecutorService = new ScheduledThreadPoolExecutor(1);
    private volatile boolean running = true;

    public TransactionProcessorv2() {
        scheduledExecutorService.scheduleWithFixedDelay(
                () -> {
                    backupWalMap();
                    backupFailedMap();
                    backupSuccessfulMap();
                },
                0,
                BACKUP_INTERVAL,
                TimeUnit.MILLISECONDS);

        failedExecutorService.scheduleWithFixedDelay(
                () -> {
                    List<Transaction> transactions = new ArrayList<>(failedMap.values());
                    transactions.forEach(queue::offer);
                    failedMap.clear();
                },
                0,
                RETRY_INTERVAL,
                TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        recover();
    }

    public void start() {
        executor.submit(() -> {
            while (running) {
                try {
                    Transaction tx = queue.poll(5, TimeUnit.SECONDS);

                    if (tx != null) {
                        processTransaction(tx);
                    }
                } catch (InterruptedException e) {
                    System.err.printf("InterruptedException in start: %s", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void processTransaction(Transaction tx) {
        try {
            System.out.println("Processing: " + tx.getId());

            Transaction transaction = successfulMap.get(tx.getId());
            if (Objects.nonNull(transaction)) {
                System.out.printf("transaction with id: %s processed before.", tx.getId());
                return;
            }

            //من اینو تغییر نمیدم تا منطق برنامه خراب نشه
            // ۱. اعتبارسنجی
            validate(tx);

            //من اینو تغییر نمیدم تا منطق برنامه خراب نشه
            // ۲. ذخیره در دیتابیس (شبیه‌سازی)
            saveToDatabase(tx);

            //من اینو تغییر نمیدم تا منطق برنامه خراب نشه
            // ۳. ارسال به سیستم خارجی (شبیه‌سازی)
            sendToExternal(tx);

            successfulMap.putIfAbsent(tx.getId(), tx);
            walMap.remove(tx.getId());
            failedMap.remove(tx.getId());
        } catch (Exception e) {
            failedMap.putIfAbsent(tx.getId(), tx);
            System.err.printf("Exception in processTransaction: %s", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public void addTransaction(Transaction tx) {
        queue.add(tx);
        walMap.putIfAbsent(tx.getId(), tx);
    }

    public void shutdown() {
        try {
            running = false;

            executor.shutdown();
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }

            scheduledExecutorService.shutdown();
            if (!scheduledExecutorService.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduledExecutorService.shutdownNow();
            }

            failedExecutorService.shutdown();
            if (!failedExecutorService.awaitTermination(10, TimeUnit.SECONDS)) {
                failedExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.printf("Exception in processTransaction: %s", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void backupWalMap() {
        Path path = Paths.get(WAL_BACKUP_URL);
        synchronized (walMap) {
            List<Transaction> values = new ArrayList<>(walMap.values());
            backupTransaction(path, values);
            walMap.clear();
        }
    }

    private void backupFailedMap() {
        Path path = Paths.get(FAILD_BACKUP_URL);
        synchronized (failedMap) {
            List<Transaction> values = new ArrayList<>(failedMap.values());
            backupTransaction(path, values);
            failedMap.clear();
        }
    }

    private void backupSuccessfulMap() {
        Path path = Paths.get(SUCCESSFUL_BACKUP_URL);
        synchronized (successfulMap) {
            List<Transaction> values = new ArrayList<>(successfulMap.values());
            backupTransaction(path, values);
        }
    }

    private void backupTransaction(Path path, List<Transaction> transactions) {
        try {
            List<String> strings = new ArrayList<>();
            transactions.forEach(transaction -> {
                if (Objects.nonNull(transaction)) {
                    String json = gson.toJson(transaction);
                    strings.add(json);
                }
            });

            if (!strings.isEmpty()) {
                Files.write(
                        path,
                        strings,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
            }
        } catch (IOException e) {
            System.err.printf("IOException in backup path:%s, error: %s", path.toString(), e.getMessage());
        }
    }

    private void recover() {
        Map<String, Transaction> recoverdMap = new ConcurrentHashMap();

        recoverSuccessful(recoverdMap);
        recoverFailed(recoverdMap);
        recoverWal(recoverdMap);
    }

    private void recoverSuccessful(Map<String, Transaction> recoverdMap) {
        try {
            Path path = Path.of(SUCCESSFUL_BACKUP_URL);
            boolean exists = Files.exists(path);

            if (exists) {
                BufferedReader bufferedReader = Files.newBufferedReader(path);
                bufferedReader
                        .lines()
                        .forEach(line -> {
                            if (Objects.nonNull(line) && !line.isEmpty()) {
                                Transaction transaction = gson.fromJson(line, Transaction.class);
                                successfulMap.put(transaction.getId(), transaction);
                                recoverdMap.put(transaction.getId(), transaction);
                            }
                        });
            }
        } catch (IOException e) {
            System.err.printf("IOException in recoverWal: %s", e.getMessage());
        }
    }

    private void recoverFailed(Map<String, Transaction> recoverdMap) {
        try {
            Path path = Path.of(FAILD_BACKUP_URL);
            boolean exists = Files.exists(path);

            if (exists) {
                BufferedReader bufferedReader = Files.newBufferedReader(path);
                bufferedReader
                        .lines()
                        .forEach(line -> {
                            if (Objects.nonNull(line) && !line.isEmpty()) {
                                Transaction transaction = gson.fromJson(line, Transaction.class);

                                Transaction recovered = recoverdMap.get(transaction.getId());
                                if (Objects.nonNull(recovered)) {
                                    failedMap.put(transaction.getId(), transaction);
                                    queue.offer(transaction);
                                }
                            }
                        });
            }
        } catch (IOException e) {
            System.err.printf("IOException in recoverWal: %s", e.getMessage());
        }
    }

    private void recoverWal(Map<String, Transaction> recoverdMap) {
        try {
            Path path = Path.of(WAL_BACKUP_URL);
            boolean exists = Files.exists(path);

            if (exists) {
                BufferedReader bufferedReader = Files.newBufferedReader(path);
                bufferedReader
                        .lines()
                        .forEach(line -> {
                            if (Objects.nonNull(line) && !line.isEmpty()) {
                                Transaction transaction = gson.fromJson(line, Transaction.class);

                                Transaction recovered = recoverdMap.get(transaction.getId());
                                if (Objects.nonNull(recovered)) {
                                    walMap.put(transaction.getId(), transaction);
                                    queue.offer(transaction);
                                }
                            }
                        });
            }

        } catch (IOException e) {
            System.err.printf("IOException in recoverWal: %s", e.getMessage());
        }
    }


}
