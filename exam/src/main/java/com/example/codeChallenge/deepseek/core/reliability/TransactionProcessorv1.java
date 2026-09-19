package com.example.codeChallenge.deepseek.core.reliability;

import com.google.gson.Gson;

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
import java.util.stream.Collectors;

/*
 Reliability (قابلیت اطمینان)
 */

public class TransactionProcessorv1 {
    private static int BATCH_SIZE = 99;
    private volatile boolean running = true;
    private static final String BACKUP_URL = "/data/backup";
    private static final Gson GSON = new Gson();
    private static final int QUEUE_SIZE = 5000;
    private static final int BACKUP_INTERVAL = 1000;
    private final List<Transaction> walBuffer = new ArrayList<>();
    private final List<Transaction> failedTransactionQueue = new ArrayList<>();
    private final BlockingQueue<Transaction> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
    private final Map<String, Transaction> processedIdSet = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    private final ExecutorService executor = new ThreadPoolExecutor(5,
            4 * Runtime.getRuntime().availableProcessors(),
            1,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100));

    public void addTransaction(Transaction transaction) {
        transaction.setStatus(TransactionStatusEnum.PROCESSING);
        queue.add(transaction);

        synchronized (walBuffer) {
            walBuffer.add(transaction);
            if (walBuffer.size() > BATCH_SIZE) {
                List<Transaction> transactions = new ArrayList<>(walBuffer);

                walBuffer.clear();

                backup(transactions);
            }
        }
    }

    public TransactionProcessorv1() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        scheduledExecutorService.scheduleWithFixedDelay(this::periodicBackup, 1, BACKUP_INTERVAL, TimeUnit.MILLISECONDS);
        recover();
    }

    public void start() {
        executor.submit(() -> {
            while (running) {
                try {
                    Transaction transaction = queue.poll(5, TimeUnit.SECONDS);

                    if (Objects.nonNull(transaction)) {
                        transaction.setStatus(TransactionStatusEnum.PROCESSING);
                        processTransaction(transaction);
                    }
                } catch (InterruptedException e) {
                    System.out.printf("v is empty at %d", System.currentTimeMillis());
                }
            }
        });
    }

    private void processTransaction(Transaction transaction) {
        try {
            System.out.printf("Transaction %s is ready to process.", transaction.getId());

            //متد ایتو نمینویسم تا منطق سوال از بین نرود
            // ۱. اعتبارسنجی
            validate(transaction);
            System.out.printf("Transaction %s validated.", transaction.getId());

            Transaction trx = processedIdSet.get(transaction.getId());
            if (Objects.nonNull(trx)) {
                System.out.printf("Transaction %s is processed before.", transaction.getId());
                return;
            }

            //متد ایتو نمینویسم تا منطق سوال از بین نرود
            // ۲. ذخیره در دیتابیس (شبیه‌سازی)
            saveToDatabase(transaction);
            System.out.printf("Transaction %s saved to database.", transaction.getId());

            //متد ایتو نمینویسم تا منطق سوال از بین نرود
            // ۳. ارسال به سیستم خارجی (شبیه‌سازی)
            sendToExternal(transaction);
            System.out.printf("Transaction %s sent to external service.", transaction.getId());

            transaction.setStatus(TransactionStatusEnum.DONE);
            processedIdSet.put(transaction.getId(), transaction);
            System.out.printf("Transaction %s processing is done.", transaction.getId());
        } catch (Exception e) {
            transaction.setStatus(TransactionStatusEnum.FAILED);
            failedTransactionQueue.add(transaction);
        }
    }

    private void periodicBackup() {
        synchronized (walBuffer) {
            List<Transaction> transactions = new ArrayList<>(walBuffer);
            backup(transactions);
            walBuffer.clear();
        }
        synchronized (failedTransactionQueue) {
            List<Transaction> failedTransactions = new ArrayList<>(failedTransactionQueue);
            backup(failedTransactions);
            failedTransactionQueue.clear();
        }
        synchronized (processedIdSet) {
            List<Transaction> processedTransactions = new ArrayList<>(processedIdSet.values());
            processedTransactions.forEach(transaction -> transaction.setStatus(TransactionStatusEnum.DONE));
            backup(processedTransactions);
        }
    }

    private synchronized void backup(List<Transaction> transactions) {
        try {
            List<String> transactionList = transactions.stream()
                    .map(GSON::toJson)
                    .collect(Collectors.toList());

            if (transactionList.isEmpty()) {
                return;
            }

            Path path = Paths.get(BACKUP_URL);
            Files.write(
                    path,
                    transactionList,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.printf("IOException in method backupQueue: %s", e.getMessage());
        }
    }

    private void recover() {
        Path path = Paths.get(BACKUP_URL);

        boolean isFileExists = Files.exists(path);
        if (isFileExists) {
            try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
                bufferedReader.lines().forEach(transactionJson -> {
                    Transaction transaction = GSON.fromJson(transactionJson, Transaction.class);

                    if (TransactionStatusEnum.DONE.equals(transaction.getStatus())) {
                        processedIdSet.put(transaction.getId(), transaction);
                    } else {
                        queue.offer(transaction);
                    }
                });
            } catch (IOException e) {
                System.err.printf("IOException in method recover: %s", e.getMessage());
            }
        } else {
            System.out.println("Backup file is not exists");
        }
    }

    public void shutdown() {
        running = false;
        periodicBackup();

        CompletableFuture<Void> completableFuture = CompletableFuture
                .runAsync(() -> {
                    try {
                        executor.shutdown();
                        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                            executor.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        System.err.printf("InterruptedException in method shutdown: %s", e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                })
                .thenRunAsync(() -> {
                    try {
                        scheduledExecutorService.shutdown();
                        if (!scheduledExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
                            scheduledExecutorService.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        System.err.printf("InterruptedException in method shutdown: %s", e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                });

        completableFuture.join();
    }

}