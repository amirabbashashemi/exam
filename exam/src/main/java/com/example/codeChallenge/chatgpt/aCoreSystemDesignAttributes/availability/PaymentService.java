package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability;

import codeChallenge.chatgpt.eCommon.payment.PaymentGateway;
import codeChallenge.chatgpt.eCommon.payment.PaymentRequest;
import codeChallenge.chatgpt.eCommon.payment.PaymentResult;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentService {
    private static final Map<Long, PaymentResult> CACHE_RESPONSE = new ConcurrentHashMap<>();

    private PaymentGateway gateway;

    public PaymentResult pay(PaymentRequest paymentRequest) {
        try {
            PaymentResult cachedPaymentResult = CACHE_RESPONSE.get(paymentRequest.getCorrelationId());

            if (Objects.nonNull(cachedPaymentResult)) {
                return cachedPaymentResult;
            }
            return callAndCacheGatewayService(paymentRequest);
        } catch (Exception exception) {
            return retry(paymentRequest);
        }
    }

    private PaymentResult retry(PaymentRequest paymentRequest) {
        try {
            for (int i = 1; i < 4; i++) {
                Thread.sleep(i * 1000);
                return callAndCacheGatewayService(paymentRequest);
            }
        } catch (InterruptedException interruptedException) {
            String errorMessage = String.format("An exception occurred in method retry for correlationId = %d", paymentRequest.getCorrelationId());
            throw new RuntimeException(errorMessage, interruptedException);
        }
        return null;
    }

    private PaymentResult callAndCacheGatewayService(PaymentRequest paymentRequest) {
        try {
            PaymentResult paymentResult = gateway.pay(paymentRequest);
            CACHE_RESPONSE.put(paymentRequest.getCorrelationId(), paymentResult);
            return paymentResult;
        } catch (RuntimeException runtimeException) {
            String errorMessage = String.format("An exception occurred in method pay for correlationId = %d", paymentRequest.getCorrelationId());
            throw new RuntimeException(errorMessage, runtimeException);
        }
    }
}

/*
هر درخواست پرداخت باید دقیقاً یک‌بار (Exactly-once) پردازش شود
سیستم به یک Payment Gateway خارجی وصل می‌شود که:
گاهی timeout می‌دهد
گاهی duplicate response برمی‌گرداند
سرویس روی Java 21 اجرا می‌شود
crash شدن JVM یا restart شدن سرویس طبیعی است
 */