package com.example.codeChallenge.deepseek.architectureQualityAttributes.extensibility.payment.strategy.pay;

import ir.dotin.extensibility.payment.PaymentRequest;

public interface Payer {
    PayEnum getType();

    void pay(PaymentRequest paymentRequest);
}
