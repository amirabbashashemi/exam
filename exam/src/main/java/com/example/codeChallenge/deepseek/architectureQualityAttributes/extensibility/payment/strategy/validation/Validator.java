package com.example.codeChallenge.deepseek.architectureQualityAttributes.extensibility.payment.strategy.validation;

import ir.dotin.extensibility.payment.PaymentRequest;

public interface Validator {
    ValidationEnum getType();

    void validate(PaymentRequest paymentRequest);
}
