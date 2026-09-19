package com.example.codeChallenge.deepseek.core.resilience.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MessageProcessor {
    private final int MAX_ATTEMPT = 5;
    private final int QUEUE_SIZE = 5000;
    private final int MAX_CONCURRENT_PROCESS_COUNT = 50;
    private volatile boolean running = true;
    private final Semaphore semaphore = new Semaphore(MAX_CONCURRENT_PROCESS_COUNT);
    private final BlockingQueue<Message> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
    private final ExecutorService processorExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final ScheduledExecutorService retryExecutor = new ScheduledThreadPoolExecutor(1);
    private final Map<String, Message> retryMessageMap = new ConcurrentHashMap<>();
    private final Map<String, Message> dlqMessageMap = new ConcurrentHashMap<>();

    public MessageProcessor() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        retryExecutor.scheduleAtFixedRate(this::retry, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private void retry() {
        List<Message> messages = null;
        synchronized (retryMessageMap) {
            messages = new ArrayList<>(retryMessageMap.values());
            retryMessageMap.clear();
        }

        if (messages != null) {
            messages.forEach(this::addMessage);
        }
    }

    public void start() {
        processorExecutor.submit(() -> {
            while (running) {
                try {
                    Message msg = queue.poll(1000, TimeUnit.MILLISECONDS);

                    if (msg != null) {
                        processMessage(msg);
                    }
                } catch (InterruptedException e) {
                    System.err.printf("InterruptedException in method saveToDB: %s", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void processMessage(Message msg) {
        try {
            boolean acquired = semaphore.tryAcquire();
            if (!acquired) {
                System.err.println("Max process attempt");
                return;
            }

            System.out.printf("Processing: %s", msg.getId());

            // ۱. اعتبارسنجی
            validate(msg);

            // ۲. ذخیره در دیتابیس (شبیه‌سازی)
            saveToDB(msg);

            // ۳. ارسال به سیستم خارجی (شبیه‌سازی)
            callExternalServiceWithRetry(msg);
        } catch (Exception e) {
            System.err.println("Exception in method processMessage for messageId " + msg.getId() + " : " + e.getMessage());
        } finally {
            semaphore.release();
        }
    }

    //اینو تغییر نمیدم که منطق سوال دستکاری نشه
    private void validate(Message msg) {
        if (msg.getAmount() <= 0) {
            dlqMessageMap.putIfAbsent(msg.getId(), msg);
            String errorMessage = String.format("Invalid amount: %s", msg.getId());
            System.err.printf(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void saveToDB(Message msg) {
        try {
            //اینو تغییر نمیدم که منطق سوال دستکاری نشه
            Thread.sleep(100);
            System.out.printf("Saved to DB: %s", msg.getId());
        } catch (InterruptedException e) {
            System.err.printf("InterruptedException in method saveToDB: %s", e.getMessage());

            addToRetryMap(msg);

            Thread.currentThread().interrupt();
        }
    }

    private void callExternalServiceWithRetry(Message msg) {
        int delay = 500;
        int attempt = 0;

        while (attempt < MAX_ATTEMPT) {
            try {
                callExternalService(msg);
                retryMessageMap.remove(msg.getId());
                return;
            } catch (Exception e) {
                delay *= 2;
                attempt++;

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException interruptedException) {
                    System.err.printf("InterruptedException in method callExternalServiceWithRetry: %s", interruptedException.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }

        retryMessageMap.remove(msg.getId());
        dlqMessageMap.putIfAbsent(msg.getId(), msg);
    }

    private void callExternalService(Message msg) throws InterruptedException {
        try {
            //اینو تغییر نمیدم که منطق سوال دستکاری نشه
            Thread.sleep(50);
            if (Math.random() > 0.9) { // ۱۰٪ خطا
                throw new RuntimeException("External system error!");
            }
            System.out.printf("Sent to external: %s", msg.getId());
        } catch (Exception e) {
            System.err.printf("Failed to send: " + msg.getId() + " - " + e.getMessage());

            addToRetryMap(msg);

            throw e;
        }
    }

    private void addToRetryMap(Message msg) {
        //فقط یکبار retry شود.
        Message message = dlqMessageMap.get(msg.getId());

        if (message == null) {
            retryMessageMap.putIfAbsent(msg.getId(), msg);
        }
    }

    public void addMessage(Message msg) {
        boolean offered = queue.offer(msg);

        if (!offered) {
            System.err.printf("queue is full messageId: %s", msg.getId());
        }
    }

    public void shutdown() {
        running = false;

        try {
            processorExecutor.shutdown();
            if (!processorExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                processorExecutor.shutdownNow();
            }

            retryExecutor.shutdown();
            if (!retryExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                retryExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.printf("InterruptedException in method shutdown: %s", e.getMessage());
            Thread.currentThread().interrupt();
        }

    }
}