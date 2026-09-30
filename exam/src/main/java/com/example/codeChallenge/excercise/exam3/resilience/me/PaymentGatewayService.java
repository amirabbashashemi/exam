package com.example.codeChallenge.excercise.exam3.resilience.me;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class PaymentGatewayService {
    private final int maxTryCount;
    private final Semaphore bulkhead;
    private final CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
            .custom()
            .slidingWindowSize(100)
            .failureRateThreshold(50)
            .slowCallRateThreshold(50)
            .slowCallDurationThreshold(Duration.ofMillis(1000))
            .waitDurationInOpenState(Duration.ofMillis(1000))
            .build();
    private final CircuitBreaker circuitBreaker = CircuitBreaker.of("sample-cb", circuitBreakerConfig);

    public PaymentGatewayService(int maxTryCount, int semaphoreCount) {
        this.maxTryCount = maxTryCount;
        this.bulkhead = new Semaphore(semaphoreCount);
    }


    public PaymentResult processPayment(PaymentRequest paymentRequest) {
        if (!circuitBreaker.tryAcquirePermission()) {
            return fallback(paymentRequest.getId(), "Circuit is OPEN");
        }
        try {
            boolean acquired = bulkhead.tryAcquire(5, TimeUnit.SECONDS);
            if (!acquired) {
                return fallback(paymentRequest.getId(), "Bulkhead full");
            }
            try {
                return processWithRetry(paymentRequest);
            } finally {
                bulkhead.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return fallback(paymentRequest.getId(), "Interrupted");
        } finally {
            circuitBreaker.releasePermission();
        }
    }

    private PaymentResult processWithRetry(PaymentRequest request) {
        for (int attempt = 0; attempt <= maxTryCount; attempt++) {
            try {
                PaymentResult result = callExternalBankApi(request);
                circuitBreaker.onSuccess(0, TimeUnit.MILLISECONDS);
                return result;
            } catch (Exception e) {
                circuitBreaker.onError(0, TimeUnit.MILLISECONDS, e);
                if (attempt == maxTryCount) {
                    return fallback(request.getId(), "Max retries reached");
                }

                sleepBackoff(attempt);
            }
        }

        return fallback(request.getId(), "Unknown");
    }

    private void sleepBackoff(int attempt) {
        try {
            long pow = (long) Math.pow(2, attempt) * 100;
            Thread.sleep(pow);
        } catch (InterruptedException e) {
            System.err.printf("An exception occurred in method sleepBackoff: %s", e.getMessage());
        }

    }

    private PaymentResult fallback(Long id, String str) {
        return new PaymentResult(id, str);
    }

    private PaymentResult callExternalBankApi(PaymentRequest request) {
        // شبیه‌سازی فراخوانی API
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.err.printf("Error in method callExternalBankApi error message is: %s", e.getMessage());
            throw new RuntimeException(e);
        }
        return new PaymentResult(request.getId(), "SUCCESS");
    }


}