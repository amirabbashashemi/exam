package com.example.codeChallenge.deepseek.core.robustness.pay;

import java.net.http.*;
import java.net.URI;
import java.time.*;
import java.util.*;

public class PaymentVerificationServiceOld {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Map<String, Payment> payments = new HashMap<>();

    public Payment verifyPayment(String paymentId) {
        // ۱. دریافت اطلاعات پرداخت
        Payment payment = getPayment(paymentId);
        if (payment == null) {
            throw new RuntimeException("Payment not found");
        }

        // ۲. تماس با API بانک
        String url = "https://api.bank.com/verify/" + paymentId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // ۳. پردازش پاسخ
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

    private Payment getPayment(String paymentId) {
        return payments.get(paymentId);
    }
}