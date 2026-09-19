package com.example.codeChallenge.deepseek.core.reliability;
import java.util.*;
import java.util.concurrent.*;

public class TransactionProcessorOld {
    private final Queue<Transaction> queue = new LinkedList<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final List<Transaction> processed = new ArrayList<>();
    private volatile boolean running = true;

    public void start() {
        executor.submit(() -> {
            while (running) {
                Transaction tx = queue.poll();
                if (tx != null) {
                    processTransaction(tx);
                }
            }
        });
    }

    private void processTransaction(Transaction tx) {
        System.out.println("Processing: " + tx.getId());

        // ۱. اعتبارسنجی
        validate(tx);

        // ۲. ذخیره در دیتابیس (شبیه‌سازی)
        saveToDatabase(tx);

        // ۳. ارسال به سیستم خارجی (شبیه‌سازی)
        sendToExternal(tx);

        processed.add(tx);
    }

    public void addTransaction(Transaction tx) {
        queue.add(tx);
    }

    public void shutdown() {
        running = false;
        executor.shutdown();
    }
}