package com.example.codeChallenge.deepseek.core.extensibility.payment.strategy.validation;

import codeChallenge.deepseek.core.extensibility.payment.PaymentRequest;

public interface Validator {
    ValidationEnum getType();

    void validate(PaymentRequest paymentRequest);
}
