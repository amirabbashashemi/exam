package com.example.codeChallenge.deepseek.core.resilience.pay;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/*
Resilience (تاب‌آوری در برابر خطا) - External Service with Circuit Breaker + Retry + Fallback
 */
public class PaymentServiceOld {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Map<String, Payment> payments = new HashMap<>();

    public Payment verifyPayment(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            throw new RuntimeException("Payment not found");
        }

        // تماس با سیستم خارجی
        String url = "https://api.payment-gateway.com/verify/" + paymentId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String body = response.body();
                if (body.contains("verified")) {
                    payment.setStatus("VERIFIED");
                    payments.put(paymentId, payment);
                    return payment;
                } else {
                    payment.setStatus("FAILED");
                    payments.put(paymentId, payment);
                    return payment;
                }
            } else {
                payment.setStatus("ERROR");
                payments.put(paymentId, payment);
                return payment;
            }
        } catch (Exception e) {
            payment.setStatus("ERROR");
            payments.put(paymentId, payment);
            return payment;
        }
    }
}