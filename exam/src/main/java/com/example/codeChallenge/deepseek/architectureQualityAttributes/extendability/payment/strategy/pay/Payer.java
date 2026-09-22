package com.example.codeChallenge.deepseek.architectureQualityAttributes.extendability.payment.strategy.pay;

import codeChallenge.deepseek.core.extensibility.payment.PaymentRequest;

public interface Payer {
    PayEnum getType();

    void pay(PaymentRequest paymentRequest);
}
