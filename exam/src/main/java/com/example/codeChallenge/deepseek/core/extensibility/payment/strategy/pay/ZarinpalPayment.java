package com.example.codeChallenge.deepseek.core.extensibility.payment.strategy.pay;

import codeChallenge.deepseek.core.extensibility.payment.Payment;
import codeChallenge.deepseek.core.extensibility.payment.PaymentRequest;

public class ZarinpalPayment extends BasePayment implements Payer {
    @Override
    public PayEnum getType() {
        return PayEnum.ZARINPAL;
    }

    @Override
    public void pay(PaymentRequest paymentRequest) {
        System.out.println("Processing Zarinpal payment: " + paymentRequest.getId());

        String merchantId = "zarinpal_merchant_123";
        String callbackUrl = "https://example.com/callback";

        boolean success = callZarinpalAPI(paymentRequest, merchantId, callbackUrl);

        if (success) {
            Payment payment = new Payment(paymentRequest.getId(), "ZARINPAL", paymentRequest.getAmount(), "SUCCESS");
            PAYMENT_MAP.put(paymentRequest.getId(), payment);
            sendZarinpalNotification(paymentRequest);
        } else {
            Payment payment = new Payment(paymentRequest.getId(), "ZARINPAL", paymentRequest.getAmount(), "FAILED");
            PAYMENT_MAP.put(paymentRequest.getId(), payment);
        }
    }

    //اینو تغییر ندادم که منطق اولیه تغییر نکنه
    private boolean callZarinpalAPI(PaymentRequest request, String merchantId, String callbackUrl) {
        System.out.println("Calling Zarinpal API with merchantId: " + merchantId);
        return Math.random() > 0.2;
    }

    //اینو تغییر ندادم که منطق اولیه تغییر نکنه
    private void sendZarinpalNotification(PaymentRequest request) {
        System.out.println("Zarinpal notification sent for: " + request.getId());
    }
}
