package com.example.codeChallenge.deepseek.core.continuity.trx;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*
مشکلات فعلی:

Single Point of Failure – اگر سرور اصلی از کار بیفتد، کل سیستم متوقف می‌شود
هیچ مکانیزم Failover برای انتقال به سرور پشتیبان وجود ندارد
هیچ Health Check برای تشخیص زنده بودن سرور اصلی وجود ندارد
هیچ Data Replication بین سرور اصلی و پشتیبان وجود ندارد
در صورت Disaster، داده‌ها از دست می‌روند (هیچ Recovery Point وجود ندارد)
زمان بازیابی (Recovery Time) نامشخص است
 */
public class TransactionService {
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

        // ۲. ذخیره در دیتابیس
        saveToDatabase(tx);

        // ۳. ارسال به سیستم خارجی
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

    //----------------------------------------------------------------------------------------------------
    //    شبیه سازی ولیدیشن
    private void validate(Transaction transaction) {
        hold(100);
    }

    //    شبیه  سازی ذخیره در دیتابیس
    private void saveToDatabase(Transaction transaction) {
        hold(1000);
    }

    //    شبیه سازی ارسال به سیستم خارجی
    private void sendToExternal(Transaction transaction) {
        hold(2000);
    }

    private void hold(long millisecond){
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            System.err.printf("InterruptedException in method validate: %s", e.getMessage());
        }

    }
}
