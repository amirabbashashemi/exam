package com.example.codeChallenge.deepseek.core.extensibility.payment.strategy.pay;

import codeChallenge.deepseek.core.extensibility.payment.Payment;
import codeChallenge.deepseek.core.extensibility.payment.PaymentRequest;

public class PayPalPayment extends BasePayment implements Payer{
    @Override
    public PayEnum getType() {
        return PayEnum.PAYPAL;
    }

    @Override
    public void pay(PaymentRequest paymentRequest) {
        // PayPal
        System.out.println("Processing PayPal payment: " + paymentRequest.getId());

        // تنظیمات PayPal
        String clientId = "paypal_client_123";
        String secret = "paypal_secret_456";

        // فراخوانی API PayPal
        boolean success = callPayPalAPI(paymentRequest, clientId, secret);

        if (success) {
            Payment payment = new Payment(paymentRequest.getId(), "PAYPAL", paymentRequest.getAmount(), "SUCCESS");
            PAYMENT_MAP.put(paymentRequest.getId(), payment);
            sendPayPalNotification(paymentRequest);
        } else {
            Payment payment = new Payment(paymentRequest.getId(), "PAYPAL", paymentRequest.getAmount(), "FAILED");
            PAYMENT_MAP.put(paymentRequest.getId(), payment);
        }
    }

    //اینو تغییر ندادم که منطق اولیه تغییر نکنه
    private boolean callPayPalAPI(PaymentRequest request, String clientId, String secret) {
        // شبیه‌سازی تماس با PayPal
        System.out.println("Calling PayPal API with clientId: " + clientId);
        return Math.random() > 0.2;
    }

    //اینو تغییر ندادم که منطق اولیه تغییر نکنه
    private void sendPayPalNotification(PaymentRequest request) {
        System.out.println("PayPal notification sent for: " + request.getId());
    }
}
