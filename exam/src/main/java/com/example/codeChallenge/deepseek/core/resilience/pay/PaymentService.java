package com.example.codeChallenge.deepseek.core.resilience.pay;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;

public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    // ==================== Config ====================
    private static final int MAX_RETRIES = 3;
    private static final long BASE_DELAY_MS = 1000;
    private static final int MAX_CONCURRENT = 20;

    // ==================== Resilience4j Circuit Breaker ====================
    private final CircuitBreaker circuitBreaker = CircuitBreaker.of(
            "payment-gateway",
            CircuitBreakerConfig.custom()
                    .failureRateThreshold(50)
                    .slowCallRateThreshold(50)
                    .slowCallDurationThreshold(Duration.ofSeconds(2))
                    .slidingWindowSize(10)
                    .minimumNumberOfCalls(5)
                    .waitDurationInOpenState(Duration.ofSeconds(30))
                    .permittedNumberOfCallsInHalfOpenState(3)
                    .build()
    );

    // ==================== Components ====================
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();
    private final Semaphore bulkhead = new Semaphore(MAX_CONCURRENT);
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public PaymentService() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    // ==================== Main Method ====================
    public Payment verifyPayment(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            return fallback(paymentId);
        }


        try {
            boolean acquired = bulkhead.tryAcquire();
            if (!acquired) {
                log.warn("Bulkhead limit reached for {}", paymentId);
                return fallback(paymentId);
            }


            // 2️⃣ Retry with Exponential Backoff + Circuit Breaker
            return callWithRetry(paymentId);
        } finally {
            bulkhead.release();
            ;
        }
    }

    private Payment callWithRetry(String paymentId) {
        int attempt = 0;
        long delay = BASE_DELAY_MS;

        while (attempt < MAX_RETRIES) {
            try {
                // 3️⃣ Circuit Breaker Check
                if (!circuitBreaker.tryAcquirePermission()) {
                    log.warn("Circuit Breaker OPEN for {}", paymentId);
                    return fallback(paymentId);
                }

                try {
                    // 4️⃣ Call External Service
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(String.format("https://api.payment-gateway.com/verify/%s", paymentId)))
                            .timeout(Duration.ofSeconds(5))
                            .GET()
                            .build();

                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    // 5️⃣ Parse Response
                    Payment payment = payments.get(paymentId);
                    if (response.statusCode() == 200 && response.body().contains("verified")) {
                        payment.setStatus("VERIFIED");
                        payments.put(paymentId, payment);
                        return payment;
                    } else {
                        payment.setStatus("FAILED");
                        payments.put(paymentId, payment);
                        return payment;
                    }

                } finally {
                    // 6️⃣ Always release permission
                    circuitBreaker.releasePermission();
                }
            } catch (Exception e) {
                log.warn("Attempt {} failed for {}: {}", attempt, paymentId, e.getMessage());
            } finally {
                attempt++;

                try {
                    Thread.sleep(delay);
                    delay *= 2;
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return fallback(paymentId);
                }
            }
        }

        return fallback(paymentId);
    }

    // ==================== Fallback ====================
    private Payment fallback(String paymentId) {
        log.warn("Using fallback for {}", paymentId);
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            payment = new Payment(paymentId);
        }
        payment.setStatus("PENDING_MANUAL");
        payments.put(paymentId, payment);
        return payment;
    }

    // ==================== Helper ====================
    public void addPayment(Payment payment) {
        payments.put(payment.getId(), payment);
    }

    // ==================== Shutdown ====================
    private void shutdown() {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}