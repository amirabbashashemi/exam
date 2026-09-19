package com.example.codeChallenge.deepseek.core.resilience.message;

import java.util.*;
import java.util.concurrent.*;

public class MessageProcessor {
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
            failedMessages.add(msg);
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