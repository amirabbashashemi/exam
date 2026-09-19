package com.example.codeChallenge.deepseek.core.extensibility.payment.strategy.pay;

import codeChallenge.deepseek.core.extensibility.payment.Payment;
import codeChallenge.deepseek.core.extensibility.payment.PaymentRequest;

public class StripePayment extends BasePayment implements Payer{
    @Override
    public PayEnum getType() {
        return PayEnum.STRIPE;
    }

    @Override
    public void pay(PaymentRequest paymentRequest) {
        System.out.println("Processing Stripe payment: " + paymentRequest.getId());

        String apiKey = "stripe_api_key_789";
        String webhookSecret = "stripe_webhook_secret";

        boolean success = callStripeAPI(paymentRequest, apiKey, webhookSecret);

        if (success) {
            Payment payment = new Payment(paymentRequest.getId(), "STRIPE", paymentRequest.getAmount(), "SUCCESS");
            PAYMENT_MAP.put(paymentRequest.getId(), payment);
            sendStripeNotification(paymentRequest);
        } else {
            Payment payment = new Payment(paymentRequest.getId(), "STRIPE", paymentRequest.getAmount(), "FAILED");
            PAYMENT_MAP.put(paymentRequest.getId(), payment);
        }
    }

    //اینو تغییر ندادم که منطق اولیه تغییر نکنه
    private boolean callStripeAPI(PaymentRequest request, String apiKey, String webhookSecret) {
        System.out.println("Calling Stripe API with apiKey: " + apiKey);
        return Math.random() > 0.2;
    }

    //اینو تغییر ندادم که منطق اولیه تغییر نکنه
    private void sendStripeNotification(PaymentRequest request) {
        System.out.println("Stripe notification sent for: " + request.getId());
    }
}
