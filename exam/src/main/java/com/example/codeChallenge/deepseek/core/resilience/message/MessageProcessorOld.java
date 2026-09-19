package com.example.codeChallenge.deepseek.core.resilience.message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class MessageProcessorOld {
    private volatile boolean running = true;
    private static final int INPUT_QUEUE_SIZE = 10000;
    private final BlockingQueue<Message> publishQueue = new ArrayBlockingQueue<>(INPUT_QUEUE_SIZE);
    private final ExecutorService subscriberExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    private final List<Message> failedMessages = new ArrayList<>();
    private final List<Message> dalMessages = new ArrayList<>();

    public MessageProcessorOld() {
        scheduledExecutorService.scheduleAtFixedRate(() -> retry(), 0, 100, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void retry() {
        synchronized (failedMessages) {
            failedMessages.forEach(this::addMessage);
            failedMessages.clear();
        }
        synchronized (dalMessages) {
            dalMessages.forEach(this::addMessage);
            dalMessages.clear();
        }
    }

    public void start() {
        subscriberExecutor.submit(() -> {
            while (running) {
                try {
                    Message message = publishQueue.poll(1, TimeUnit.SECONDS);

                    if (message != null) {
                        processMessage(message);
                    }
                } catch (InterruptedException e) {
                    System.err.printf("InterruptedException in method start: %s", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void processMessage(Message message) {
        try {
            System.out.println("Processing: " + message.getId());

            validate(message);

            // ۲. ذخیره در دیتابیس (شبیه‌سازی)
            saveToDB(message);

            // ۳. ارسال به سیستم خارجی (شبیه‌سازی)
            callExternalService(message);
        } catch (Exception e) {
            System.err.println("Exception in method processMessage: %s" + message.getId());
            dalMessages.add(message);
        }
    }

    //اینو تغییر نمیدم که شبیه سازی سوال از بین نره
    private void callExternalService(Message message) {
        try {
            Thread.sleep(50);
            if (Math.random() > 0.9) { // ۱۰٪ خطا
                throw new RuntimeException("External system error!");
            }
            System.out.printf("Sent to external: %s", message.getId());
        } catch (Exception e) {
            System.err.println("Failed to send: " + message.getId() + " - " + e.getMessage());
            failedMessages.add(message);
        }
    }

    //اینو تغییر نمیدم که شبیه سازی سوال از بین نره
    private static void saveToDB(Message message) {
        try {
            Thread.sleep(100);
            System.out.printf("Saved to DB: %s", message.getId());
        } catch (InterruptedException e) {
            System.err.printf("InterruptedException in method saveToDB: %s", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void validate(Message message) {
        if (message.getAmount() <= 0) {
            System.err.printf("Invalid amount: %s", message.getId());
            throw new IllegalArgumentException("Invalid amount for messageId " + message.getId());
        }
    }

    public void addMessage(Message message) {
        boolean offer = publishQueue.offer(message);

        if (!offer) {
            System.out.println("Input queue is full");
        }
    }

    public void shutdown() {
        try {
            running = false;

            subscriberExecutor.shutdown();
            if (!subscriberExecutor.awaitTermination(20, TimeUnit.SECONDS)) {
                subscriberExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.printf("InterruptedException in method shutdown: %s", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}