package com.example.codeChallenge.deepseek.core.availability.transaction;

import java.util.*;
import java.util.concurrent.*;

public class TransactionServiceOld {
    private final Queue<Transaction> queue = new LinkedList<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
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
        // ۱. اعتبارسنجی
        validate(tx);

        // ۲. ذخیره در دیتابیس (شبیه‌سازی)
        saveToDatabase(tx);

        // ۳. ارسال به سیستم خارجی (شبیه‌سازی)
        sendToExternalSystem(tx);
    }

    public void addTransaction(Transaction tx) {
        queue.add(tx);
    }

    public void shutdown() {
        running = false;
        executor.shutdown();
    }
}