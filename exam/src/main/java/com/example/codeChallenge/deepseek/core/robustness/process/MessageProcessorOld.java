package com.example.codeChallenge.deepseek.core.robustness.process;

import java.util.*;
import java.util.concurrent.*;
/*
مشکلات فعلی:

اگر پردازش یک پیام با خطا مواجه شود، پیام از دست می‌رود (فقط در لیست failedMessages ذخیره می‌شود)
هیچ Retry mechanism برای خطاهای موقت وجود ندارد
خطاهای مختلف (دیتابیس، شبکه، اعتبارسنجی) به یک شکل مدیریت می‌شوند
هیچ Dead Letter Queue (DLQ) برای پیام‌های غیرقابل بازیابی وجود ندارد
اگر خطایی در processMessage() رخ دهد، ترد از بین می‌رود
بدون Logging مناسب برای خطاها
بدون مکانیزم برای بازیابی پیام‌های failed بعد از رفع مشکل
 */
public class MessageProcessorOld {
    private final Queue<Message> queue = new LinkedList<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final List<Message> failedMessages = new ArrayList<>();
    private volatile boolean running = true;

    public void start() {
        executor.submit(() -> {
            while (running) {
                Message msg = queue.poll();
                if (msg != null) {
                    processMessage(msg);
                }
            }
        });
    }

    private void processMessage(Message msg) {
        System.out.println("Processing: " + msg.getId());

        // ۱. اعتبارسنجی
        if (msg.getAmount() <= 0) {
            System.err.println("Invalid amount: " + msg.getId());
            return;
        }

        // ۲. ذخیره در دیتابیس (شبیه‌سازی)
        try {
            Thread.sleep(100);
            System.out.println("Saved to DB: " + msg.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // ۳. ارسال به سیستم خارجی (شبیه‌سازی)
        try {
            Thread.sleep(50);
            if (Math.random() > 0.9) { // ۱۰٪ خطا
                throw new RuntimeException("External system error!");
            }
            System.out.println("Sent to external: " + msg.getId());
        } catch (Exception e) {
            System.err.println("Failed to send: " + msg.getId() + " - " + e.getMessage());
            failedMessages.add(msg); // ← فقط ذخیره می‌کند، نه Retry!
        }
    }

    public void addMessage(Message msg) {
        queue.add(msg);
    }

    public void shutdown() {
        running = false;
        executor.shutdown();
    }
}