package com.example.codeChallenge.deepseek.core.robustness.process;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MessageProcessor {
    private final static int MAX_ATTEMPT = 3;
    private final ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<>(100);
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final ExecutorService dbExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final BlockingQueue<Message> dlqMessages = new ArrayBlockingQueue<>(100);
    private final BlockingQueue<Message> failedMessages = new ArrayBlockingQueue<>(100);
    private volatile boolean running = true;

    public MessageProcessor() {
        start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void processMessage(Message message) {
        try {
            System.out.println("Processing: " + message.getId());

            // ۱. اعتبارسنجی
            validateMessage(message);

            // ۲. ذخیره در دیتابیس (شبیه‌سازی)
            saveToDB(message);

            // ۳. ارسال به سیستم خارجی (شبیه‌سازی)
            callExternalServiceUsingRetry(message);
        } catch (Exception e) {
            System.err.printf("Exception on method processMessage: %s", e.getMessage());
        }
    }

    private void validateMessage(Message message) {
        if (message.getAmount() <= 0) {
            System.err.printf("Invalid amount: %s", message.getAmount());
            offerToDLQMessages(message);
            throw new IllegalArgumentException(STR."Invalid amount: \{message.getId()}");
        }
    }

    private void saveToDB(Message message) {
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            try {
                //این رو تغییر نمیدم که منطق سوال خراب نشه
                Thread.sleep(100);
                System.out.println("Saved to DB: " + message.getId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                offerToDLQMessages(message);
            }
        }, dbExecutor);

        try {
            completableFuture.get(5, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            System.err.printf("ExecutionException in Saved to DB: %s", message.getId());
            offerToDLQMessages(message);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            System.err.printf("InterruptedException in Saved to DB: %s", message.getId());
            Thread.currentThread().interrupt();
            offerToDLQMessages(message);
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            System.err.printf("TimeoutException in Saved to DB: %s", message.getId());
            offerToDLQMessages(message);
            throw new RuntimeException(e);
        }
    }

    private void callExternalServiceUsingRetry(Message message) {
        int maxAttempt = 1;

        while (true) {
            if (maxAttempt > MAX_ATTEMPT) {
                dlqMessages.add(message);
                return;
            }

            try {
                double pow = 1000 * Math.pow(2, maxAttempt);
                Thread.sleep((long) pow);
                callExternalService(message);

                return;
            } catch (Exception e) {
                System.err.printf("Exception on method callExternalServiceUsingRetry: %s", e.getMessage());
                maxAttempt++;
            }
        }
    }

    //این رو تغییر نمیدم که منطق سوال خراب نشه
    private void callExternalService(Message message) throws InterruptedException {
        try {
            Thread.sleep(50);
            if (Math.random() > 0.9) { // ۱۰٪ خطا
                throw new RuntimeException("External system error!");
            }
            System.out.println("Sent to external: " + message.getId());
        } catch (RuntimeException e) {
            System.err.println("RuntimeException to send: " + message.getId() + " - " + e.getMessage());
            failedMessages.add(message);
            throw e;
        } catch (InterruptedException e) {
            System.err.println("InterruptedException to send: " + message.getId() + " - " + e.getMessage());
            failedMessages.add(message);
            throw e;
        }
    }

    private void offerToDLQMessages(Message message) {
        try {
            dlqMessages.offer(message, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.printf("Deal letter queue is full. %s", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void addMessage(Message message) {
        queue.add(message);
    }

    public void start() {
        executor.submit(() -> {
            while (running) {
                try {
                    Message message = queue.poll(5, TimeUnit.SECONDS);
                    if (message != null) {
                        processMessage(message);
                    }
                } catch (InterruptedException e) {
                    System.err.printf("InterruptedException in method start. %s", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public void shutdown() {
        try {
            running = false;

            executor.shutdown();
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }

            dbExecutor.shutdown();
            if (!dbExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                dbExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.printf("InterruptedException on method shutdown: %s", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public List<Message> getFailedMessages() {
        return new ArrayList<>(failedMessages);
    }

    public List<Message> getDLQMessages() {
        return new ArrayList<>(dlqMessages);
    }
}