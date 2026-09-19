package com.example.codeChallenge.deepseek.core.extensibility.payment.strategy.validation;

import codeChallenge.deepseek.core.extensibility.payment.PaymentRequest;

public class CurrencyValidator implements Validator {
    @Override
    public ValidationEnum getType() {
        return ValidationEnum.CURRENCY;
    }

    @Override
    public void validate(PaymentRequest paymentRequest) {
        if (paymentRequest.getCurrency() == null) {
            throw new IllegalArgumentException("Currency required");
        }
    }
}
