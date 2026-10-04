package com.example.codeChallenge.deepseek.core.availability.order;




import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class OrderPaymentServiceDS {
    private final CircuitBreaker circuitBreaker;
    private final ExecutorService executor;
    private final BlockingQueue<Order> deadLetter;
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final AtomicBoolean running = new AtomicBoolean(true);

    // Metrics
    private final AtomicLong successCount = new AtomicLong();
    private final AtomicLong failureCount = new AtomicLong();
    private final AtomicLong rejectedCount = new AtomicLong();

    public OrderPaymentServiceDS() {
        // ۱. Circuit Breaker با تنظیمات درست
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50.0f)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(2)
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .build();
        this.circuitBreaker = CircuitBreaker.of("external-payment", config);

        // ۲. Virtual Threads + Graceful Shutdown
        this.executor = Executors.newVirtualThreadPerTaskExecutor();

        // ۳. DLQ محدود
        this.deadLetter = new ArrayBlockingQueue<>(10_000);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    // ------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------
    public void processOrder(Order order) {
        if (!running.get()) {
            throw new IllegalStateException("Service is shutting down");
        }
        try {
            executor.submit(() -> handleOrder(order));
        } catch (RejectedExecutionException e) {
            rejectedCount.incrementAndGet();
            order.setStatus("REJECTED");
            enqueueDeadLetter(order);
            System.err.println("Executor rejected order: " + order.getId());
        }
    }

    private void handleOrder(Order order) {
        try {
            validateOrder(order);

            // ۴. Circuit Breaker با Timeout داخلی
            PaymentResult result = circuitBreaker.executeSupplier(() ->
                    callExternalWithTimeout(order)
            );

            if (result.isSuccess()) {
                order.setStatus("PAID");
                orders.put(order.getId(), order);
                successCount.incrementAndGet();
            } else {
                order.setStatus("FAILED");
                failureCount.incrementAndGet();
                enqueueDeadLetter(order);
            }
        } catch (Exception e) {
            order.setStatus("ERROR");
            failureCount.incrementAndGet();
            enqueueDeadLetter(order);
            System.err.println("Error processing order " + order.getId() + ": " + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // External Call + Timeout
    // ------------------------------------------------------------
    private PaymentResult callExternalWithTimeout(Order order) {
        try {
            // در یک Virtual Thread جداگانه با Timeout
            CompletableFuture<PaymentResult> future = CompletableFuture
                    .supplyAsync(
                            () -> callExternalPaymentSystem(order),
                            executor
                    );
            return future.get(2, TimeUnit.SECONDS); // Timeout 2 ثانیه
        } catch (TimeoutException e) {
            throw new RuntimeException("Payment system timeout", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private PaymentResult callExternalPaymentSystem(Order order) {
        try {
            Thread.sleep(200); // شبیه‌سازی پاسخ معمولی
            if (Math.random() > 0.7) {
                throw new RuntimeException("Payment system error");
            }
            return new PaymentResult(true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new PaymentResult(false);
        }
    }

    private void enqueueDeadLetter(Order order) {
        if (!deadLetter.offer(order)) {
            System.err.println("DLQ full! Order lost: " + order.getId());
        }
    }

    private void validateOrder(Order order) {
        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("Invalid order");
        }
    }

    // ------------------------------------------------------------
    // Health Check
    // ------------------------------------------------------------
    public boolean liveness() {
        return running.get();
    }

    public boolean readiness() {
        return running.get()
                && circuitBreaker.getState() != CircuitBreaker.State.OPEN
                && deadLetter.remainingCapacity() > 0;
    }

    public String getHealthStatus() {
        return String.format(
                "OrderPaymentService{state=%s, success=%d, failure=%d, rejected=%d, dlq=%d}",
                circuitBreaker.getState(),
                successCount.get(),
                failureCount.get(),
                rejectedCount.get(),
                deadLetter.size()
        );
    }

    // ------------------------------------------------------------
    // Graceful Shutdown
    // ------------------------------------------------------------
    public void shutdown() {
        System.out.println("🛑 Shutting down OrderPaymentService...");
        running.set(false);
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("✅ Shutdown complete. " + getHealthStatus());
    }
}
