package com.example.codeChallenge.deepseek.core.availability.order;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;

public class OrderPaymentService {
    private final CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
            .custom()
            .failureRateThreshold(50.0f)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .permittedNumberOfCallsInHalfOpenState(2)
            .slidingWindowSize(10)
            .minimumNumberOfCalls(5)
            .build();
    private final CircuitBreaker circuitBreaker = CircuitBreaker.of("external-service", circuitBreakerConfig);
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final BlockingQueue<Order> deadLetter = new LinkedBlockingQueue<>();

    public OrderPaymentService() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void processOrder(Order order) {
        executor.submit(() -> {
            try {
                // ۱. اعتبارسنجی
                validateOrder(order);

                // ۲. پردازش پرداخت (سیستم خارجی)
                PaymentResult result = callExternal(order);

                // ۳. به‌روزرسانی وضعیت
                if (result.isSuccess()) {
                    order.setStatus("PAID");
                    orders.put(order.getId(), order);
                } else {
                    order.setStatus("FAILED");
                }
            } catch (Exception e) {
                order.setStatus("ERROR");
                System.err.println("Error processing order: " + e.getMessage());
                deadLetter.add(order);
            }
        });
    }

    private PaymentResult callExternal(Order order) {
        PaymentResult paymentResult = null;

        if (circuitBreaker.tryAcquirePermission()) {
            try {
                paymentResult = callExternalPaymentSystem(order);
            } finally {
                circuitBreaker.releasePermission();
            }
        } else {
            paymentResult = fallback(order);
        }

        return paymentResult;

    }

    private PaymentResult fallback(Order order) {
        System.err.printf("CircuitBreaker is open. calling fallback. order id is: %s%n", order.getId());

        deadLetter.offer(order);

        return new PaymentResult(false);
    }

    private PaymentResult callExternalPaymentSystem(Order order) {
        // شبیه‌سازی تماس با سیستم خارجی
        try {
            Thread.sleep(200); // زمان پاسخگویی معمولی
            // شبیه‌سازی خطا
            if (Math.random() > 0.7) {
                throw new RuntimeException("Payment system timeout!");
            }
            return new PaymentResult(true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new PaymentResult(false);
        }
    }

    private void validateOrder(Order order) {
        // اعتبارسنجی
    }

    private void shutdown() {
        try {
            executor.shutdown();
            boolean terminated = executor.awaitTermination(10, TimeUnit.SECONDS);
            if (!terminated) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.print("Error in shutting down");
            Thread.currentThread().interrupt();
        }
    }
}
