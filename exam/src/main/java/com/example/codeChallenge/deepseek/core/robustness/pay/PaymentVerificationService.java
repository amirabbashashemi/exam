package com.example.codeChallenge.deepseek.core.robustness.pay;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;

public class PaymentVerificationService {
    private static final int MAX_ATTEMPT = 3;
    private static final String BASE_URL = "https://api.bank.com/verify/%s";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public PaymentVerificationService() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public Payment verifyPayment(String paymentId) {
        Payment payment = checkPayment(paymentId);

        HttpRequest request = createRequest(paymentId);

        callUsingRetry(request, payment, paymentId);

        return getPayment(paymentId);
    }

    private void callUsingRetry(HttpRequest httpRequest, Payment payment, String paymentId) {
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            try {
                for (int i = 0; i < MAX_ATTEMPT; i++) {
                    callAPI(httpRequest, payment);

                    if (!"ERROR".equals(payment.getStatus())) {
                        break;
                    }

                    Thread.sleep(500 * (i + 1));
                }

                if ("ERROR".equals(payment.getStatus())) {
                    fallBack(payment);
                }

                payments.put(paymentId, payment);
            } catch (IOException e) {
                System.err.printf("IOException in callUsingRetry %s", e.getMessage());
            } catch (InterruptedException e) {
                System.err.printf("InterruptedException in callUsingRetry %s", e.getMessage());
                Thread.currentThread().interrupt();
            }

        }, executorService);

        completableFuture.join();
    }

    private void fallBack(Payment payment) {
        payment.setStatus("PENDING_MANUAL");
    }

    private void callAPI(HttpRequest httpRequest, Payment payment) throws IOException, InterruptedException {
        try {
            CompletableFuture<HttpResponse<String>> future = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response = future.get(5, TimeUnit.SECONDS);

            // ۳. پردازش پاسخ
            if (response.statusCode() == 200) {
                String body = response.body();
                if (body.contains("verified")) {
                    payment.setStatus("VERIFIED");
                } else {
                    payment.setStatus("FAILED");
                }
            } else {
                payment.setStatus("ERROR");
            }
        } catch (ExecutionException e) {
            System.err.printf("ExecutionException in callUsingRetry %s", e.getMessage());
        } catch (TimeoutException e) {
            System.err.printf("TimeoutException in callUsingRetry %s", e.getMessage());
        }
    }

    private static HttpRequest createRequest(String paymentId) {
        // ۲. تماس با API بانک
        String url = String.format(BASE_URL, paymentId);

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
    }

    private Payment checkPayment(String paymentId) {
        Payment payment = payments.get(paymentId);

        if (payment == null) {
            throw new IllegalArgumentException("Payment not found");
        }
        return payment;
    }

    private Payment getPayment(String paymentId) {
        return payments.get(paymentId);
    }

    private void shutdown() {
        try {
            executorService.shutdown();
            boolean terminated = executorService.awaitTermination(20, TimeUnit.SECONDS);
            if (!terminated) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.printf("InterruptedException in callUsingRetry %s", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}