package com.example.codeChallenge.deepseek.core.robustness.pay;






import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentVerificationServiceDS {

    // ------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------
    private static final int MAX_ATTEMPT = 3;
    private static final String BASE_URL = "https://api.bank.com/verify/%s";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final long INITIAL_BACKOFF_MS = 500;
    private static final int MAX_PAYMENT_ID_LENGTH = 64;

    // ------------------------------------------------------------
    // Dependencies (Constructor Injection)
    // ------------------------------------------------------------
    private final HttpClient httpClient;
    private final ExecutorService executorService;
    private final DeadLetterQueue deadLetterQueue;

    // ------------------------------------------------------------
    // State
    // ------------------------------------------------------------
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();
    private final AtomicLong verifiedCount = new AtomicLong(0);
    private final AtomicLong failedCount = new AtomicLong(0);
    private final AtomicLong dlqCount = new AtomicLong(0);

    public PaymentVerificationServiceDS(ExecutorService executorService,
                                      DeadLetterQueue deadLetterQueue) {
        this.executorService = Objects.requireNonNull(executorService, "executorService");
        this.deadLetterQueue = Objects.requireNonNull(deadLetterQueue, "deadLetterQueue");
        this.httpClient = HttpClient.newBuilder()
                .executor(executorService)
                .connectTimeout(CONNECT_TIMEOUT)
                .build();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    // ------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------
    public void registerPayment(Payment payment) {
        validatePayment(payment);
        payments.putIfAbsent(payment.getId(), payment);
    }

    public Payment verifyPayment(String paymentId) {
        // ۱. Validation ورودی
        validatePaymentId(paymentId);

        // ۲. چک وجود Payment
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found: " + paymentId);
        }

        // ۳. ساخت Request با Validation
        HttpRequest request;
        try {
            request = createRequest(paymentId);
        } catch (URISyntaxException e) {
            // آدرس غیرقابل ساخت → DLQ
            handleDlq(payment, "Invalid URL for paymentId: " + paymentId, e);
            return payment;
        }

        // ۴. تلاش با Retry
        callUsingRetry(request, payment);

        // ۵. ثبت متریک
        if ("VERIFIED".equals(payment.getStatus())) {
            verifiedCount.incrementAndGet();
        } else if ("FAILED".equals(payment.getStatus())) {
            failedCount.incrementAndGet();
        }

        return payment;
    }

    // ------------------------------------------------------------
    // Validation
    // ------------------------------------------------------------
    private void validatePaymentId(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("paymentId must not be null or blank");
        }
        if (paymentId.length() > MAX_PAYMENT_ID_LENGTH) {
            throw new IllegalArgumentException("paymentId exceeds max length: " + MAX_PAYMENT_ID_LENGTH);
        }
        // فقط حروف و اعداد و خط تیره
        if (!paymentId.matches("^[a-zA-Z0-9\\-]+$")) {
            throw new IllegalArgumentException("paymentId contains invalid characters");
        }
    }

    private void validatePayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment must not be null");
        }
        validatePaymentId(payment.getId());
    }

    // ------------------------------------------------------------
    // Retry Logic با طبقه‌بندی خطاها
    // ------------------------------------------------------------
    private void callUsingRetry(HttpRequest request, Payment payment) {
        for (int attempt = 1; attempt <= MAX_ATTEMPT; attempt++) {
            try {
                callAPI(request, payment);

                // اگر وضعیت قابل قبول است (نه ERROR)، خارج شو
                if (!"ERROR".equals(payment.getStatus())) {
                    return;
                }

                // خطای گذرا → Retry
                logWarn("Attempt %d/%d failed for payment %s (transient error)",
                        attempt, MAX_ATTEMPT, payment.getId());

            } catch (TransientException e) {
                logWarn("Attempt %d/%d transient failure: %s", attempt, MAX_ATTEMPT, e.getMessage());
                payment.setStatus("ERROR");

            } catch (PermanentException e) {
                // خطای دائمی → دیگر تلاش نکن، برو DLQ
                logError("Permanent failure for payment %s: %s", payment.getId(), e.getMessage());
                payment.setStatus("ERROR");
                handleDlq(payment, "Permanent failure", e);
                return;

            } catch (Exception e) {
                // خطای غیرمنتظره → محافظه‌کارانه، DLQ
                logError("Unexpected error for payment %s: %s", payment.getId(), e.getMessage());
                payment.setStatus("ERROR");
                handleDlq(payment, "Unexpected error", e);
                return;
            }

            // Backoff (فقط اگر تلاش آخر نیست)
            if (attempt < MAX_ATTEMPT) {
                if (!backoff(attempt)) {
                    // اگر در Backoff قطع شدیم، برو DLQ
                    payment.setStatus("PENDING_MANUAL");
                    handleDlq(payment, "Interrupted during backoff", null);
                    return;
                }
            }
        }

        // بعد از MAX_ATTEMPT، اگر هنوز ERROR است → DLQ
        if ("ERROR".equals(payment.getStatus())) {
            fallBack(payment);
        }
    }

    // ------------------------------------------------------------
    // API Call با Validation پاسخ
    // ------------------------------------------------------------
    private void callAPI(HttpRequest request, Payment payment)
            throws TransientException, PermanentException {

        CompletableFuture<HttpResponse<String>> future =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response;
        try {
            response = future.get(REQUEST_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new TransientException("Request timed out", e);
        } catch (InterruptedException e) {
            future.cancel(true);
            Thread.currentThread().interrupt();
            throw new TransientException("Interrupted while waiting", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            // خطاهای شبکه‌ای → گذرا
            if (cause instanceof IOException) {
                throw new TransientException("Network failure", cause);
            }
            throw new PermanentException("Execution failure", cause);
        }

        // Validation پاسخ
        if (response == null) {
            throw new TransientException("Null response received", null);
        }

        int statusCode = response.statusCode();

        // طبقه‌بندی Status Code
        if (statusCode == 200) {
            validateAndSetStatus(response, payment);
        } else if (statusCode >= 500) {
            // خطای سرور → گذرا، Retry کن
            payment.setStatus("ERROR");
            throw new TransientException("Server error: " + statusCode, null);
        } else if (statusCode == 408 || statusCode == 429) {
            // Timeout یا Rate Limit → گذرا
            payment.setStatus("ERROR");
            throw new TransientException("Retryable status: " + statusCode, null);
        } else {
            // 4xx → خطای دائمی
            payment.setStatus("ERROR");
            throw new PermanentException("Client error: " + statusCode, null);
        }
    }

    private void validateAndSetStatus(HttpResponse<String> response, Payment payment)
            throws PermanentException {
        String body = response.body();

        if (body == null || body.isBlank()) {
            throw new PermanentException("Empty response body", null);
        }

        // Validation محتوای پاسخ
        if (body.contains("verified")) {
            payment.setStatus("VERIFIED");
        } else if (body.contains("failed")) {
            payment.setStatus("FAILED");
        } else {
            // پاسخ نامعتبر → DLQ
            throw new PermanentException("Unrecognized response body: " + truncate(body), null);
        }
    }

    // ------------------------------------------------------------
    // Backoff با مدیریت وقفه
    // ------------------------------------------------------------
    private boolean backoff(int attempt) {
        try {
            long delay = INITIAL_BACKOFF_MS * (1L << (attempt - 1));
            Thread.sleep(delay);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logWarn("Backoff interrupted");
            return false;
        }
    }

    // ------------------------------------------------------------
    // Fallback و DLQ
    // ------------------------------------------------------------
    private void fallBack(Payment payment) {
        payment.setStatus("PENDING_MANUAL");
        logWarn("Fallback triggered for payment %s", payment.getId());
        handleDlq(payment, "Max retries exhausted", null);
    }

    private void handleDlq(Payment payment, String reason, Throwable cause) {
        try {
            deadLetterQueue.send(payment, reason, cause);
            dlqCount.incrementAndGet();
            logInfo("Payment %s sent to DLQ. Reason: %s", payment.getId(), reason);
        } catch (Exception e) {
            // حتی DLQ هم خطا داد → لاگ کن، ولی برنامه را نکش
            logError("DLQ send failed for payment %s: %s", payment.getId(), e.getMessage());
            // حداقل وضعیت را ذخیره کن
            payment.setStatus("PENDING_MANUAL");
        }
    }

    // ------------------------------------------------------------
    // Request Builder با مدیریت URISyntaxException
    // ------------------------------------------------------------
    private static HttpRequest createRequest(String paymentId) throws URISyntaxException {
        String url = String.format(BASE_URL, paymentId);
        return HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(REQUEST_TIMEOUT)
                .GET()
                .build();
    }

    // ------------------------------------------------------------
    // Logging
    // ------------------------------------------------------------
    private void logInfo(String format, Object... args) {
        System.out.printf("[INFO] " + format + "%n", args);
    }

    private void logWarn(String format, Object... args) {
        System.err.printf("[WARN] " + format + "%n", args);
    }

    private void logError(String format, Object... args) {
        System.err.printf("[ERROR] " + format + "%n", args);
    }

    private static String truncate(String s) {
        return s.length() > 100 ? s.substring(0, 100) + "..." : s;
    }

    // ------------------------------------------------------------
    // Health Check
    // ------------------------------------------------------------
    public String getHealthStatus() {
        return String.format(
                "PaymentVerificationService{verified=%d, failed=%d, dlq=%d, payments=%d}",
                verifiedCount.get(), failedCount.get(),
                dlqCount.get(), payments.size()
        );
    }
    // ------------------------------------------------------------
    // Graceful Shutdown
    // ------------------------------------------------------------
    private void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(20, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    // ------------------------------------------------------------
    // Custom Exceptions (طبقه‌بندی خطاها)
    // ------------------------------------------------------------
    public static class TransientException extends Exception {
        public TransientException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    public static class PermanentException extends Exception {
        public PermanentException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // ------------------------------------------------------------
    // Dead Letter Queue Interface
    // ------------------------------------------------------------
    public interface DeadLetterQueue {
        void send(Payment payment, String reason, Throwable cause);
    }
}