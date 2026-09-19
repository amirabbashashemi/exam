package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability;

import codeChallenge.chatgpt.eCommon.payment.PaymentGateway;
import codeChallenge.chatgpt.eCommon.payment.PaymentRequest;
import codeChallenge.chatgpt.eCommon.payment.PaymentResult;

public class PaymentServiceOld {

    private PaymentGateway gateway;

    public PaymentResult pay(PaymentRequest request) {
        return gateway.pay(request);
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