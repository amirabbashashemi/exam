package com.example.codeChallenge.deepseek.core.resilience.message;





import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public class MessageProcessor {
    private final BlockingQueue<Message> mainQueue;
    private final int maxAttempts;
    private final long baseRetryDelayMs;
    private final Semaphore externalBulkhead;
    private final CircuitBreaker circuitBreaker;
    private final ExecutorService workerExecutor;
    private final ScheduledExecutorService retryScheduler;
    private final Queue<Message> dlq = new ConcurrentLinkedQueue<>();
    private volatile boolean running = true;

    public MessageProcessor(int queueCapacity,
                            int maxConcurrentExternal,
                            int maxAttempts,
                            long baseRetryDelayMs) {
        this.mainQueue = new ArrayBlockingQueue<>(queueCapacity);
        this.maxAttempts = maxAttempts;
        this.baseRetryDelayMs = baseRetryDelayMs;
        this.externalBulkhead = new Semaphore(maxConcurrentExternal);
        this.workerExecutor = Executors.newVirtualThreadPerTaskExecutor();
        this.retryScheduler = Executors.newScheduledThreadPool(1);
        this.circuitBreaker = CircuitBreaker.of("external-payment",
                CircuitBreakerConfig.custom()
                        .failureRateThreshold(50.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .permittedNumberOfCallsInHalfOpenState(2)
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .build()
        );

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    // ------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------
    public void start() {
        Thread.ofVirtual().name("dispatcher").start(() -> {
            while (running) {
                try {
                    Message msg = mainQueue.poll(1, TimeUnit.SECONDS);
                    if (msg != null) {
                        workerExecutor.submit(() -> processMessage(msg, 0));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public boolean addMessage(Message msg) {
        if (msg == null) return false;
        boolean offered = mainQueue.offer(msg);
        if (!offered) {
            System.err.println("Queue full, sending to DLQ: " + msg.getId());
            sendToDlq(msg);
        }
        return offered;
    }

    // ------------------------------------------------------------
    // Processing Pipeline
    // ------------------------------------------------------------
    private void processMessage(Message msg, int attempt) {
        try {
            if (!validate(msg)) {
                sendToDlq(msg);
                return;
            }
            saveToDB(msg);
            callExternalWithResilience(msg, attempt);
        } catch (Exception e) {
            System.err.println("Processing error for " + msg.getId() + ": " + e.getMessage());
            handleFailure(msg, attempt);
        }
    }

    private boolean validate(Message msg) {
        if (msg.getAmount() <= 0) {
            System.err.println("Invalid amount: " + msg.getId());
            return false;
        }
        return true;
    }

    private void saveToDB(Message msg) {
        try {
            Thread.sleep(100);
            System.out.println("Saved to DB: " + msg.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted during DB save", e);
        }
    }

    private void callExternalWithResilience(Message msg, int attempt) {
        if (!circuitBreaker.tryAcquirePermission()) {
            System.err.println("Circuit OPEN, scheduling retry for " + msg.getId());
            handleFailure(msg, attempt);
            return;
        }

        boolean acquired = false;
        try {
            acquired = externalBulkhead.tryAcquire(5, TimeUnit.SECONDS);
            if (!acquired) {
                throw new RuntimeException("Bulkhead full for " + msg.getId());
            }
            Future<?> future = workerExecutor.submit(() -> callExternalService(msg));
            future.get(3, TimeUnit.SECONDS); // Timeout
            circuitBreaker.onSuccess(0, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            circuitBreaker.onError(0, TimeUnit.SECONDS, e);
            throw new RuntimeException("External call timeout", e);
        } catch (Exception e) {
            circuitBreaker.onError(0, TimeUnit.SECONDS, e);
            throw new RuntimeException("External call failed", e);
        } finally {
            if (acquired) {
                externalBulkhead.release();
            }
        }
    }

    private void callExternalService(Message msg) {
        try {
            Thread.sleep(50);
            if (Math.random() > 0.9) { // ۱۰٪ خطا
                throw new RuntimeException("External system error!");
            }
            System.out.println("Sent to external: " + msg.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted", e);
        }
    }

    // ------------------------------------------------------------
    // Retry & DLQ
    // ------------------------------------------------------------
    private void handleFailure(Message msg, int attempt) {
        if (attempt >= maxAttempts - 1) {
            System.err.println("Max attempts reached, sending to DLQ: " + msg.getId());
            sendToDlq(msg);
        } else {
            long delay = calculateBackoff(attempt);
            System.err.println("Scheduling retry " + (attempt + 1) + " for " + msg.getId() + " in " + delay + "ms");
            retryScheduler.schedule(
                    () -> workerExecutor.submit(() -> processMessage(msg, attempt + 1)),
                    delay, TimeUnit.MILLISECONDS);
        }
    }

    private long calculateBackoff(int attempt) {
        long delay = baseRetryDelayMs * (1L << attempt);
        long jitter = ThreadLocalRandom.current().nextLong(0, delay / 2);
        return delay + jitter;
    }

    private void sendToDlq(Message msg) {
        dlq.offer(msg);
        persistToDlqFile(msg);
    }

    private void persistToDlqFile(Message msg) {
        // در محیط واقعی: نوشتن در فایل یا دیتابیس برای ماندگاری
        // اینجا فقط لاگ می‌کنیم
        System.err.println("DLQ: " + msg.getId());
    }

    // ------------------------------------------------------------
    // Graceful Shutdown
    // ------------------------------------------------------------
    public void shutdown() {
        running = false;
        System.out.println("Shutting down...");

        List<Message> remaining = new ArrayList<>();
        mainQueue.drainTo(remaining);
        remaining.forEach(this::sendToDlq);

        workerExecutor.shutdown();
        retryScheduler.shutdown();
        try {
            if (!workerExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                workerExecutor.shutdownNow();
            }
            if (!retryScheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                retryScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            workerExecutor.shutdownNow();
            retryScheduler.shutdownNow();
        }
        System.out.println("Shutdown complete. DLQ size: " + dlq.size());
    }

}