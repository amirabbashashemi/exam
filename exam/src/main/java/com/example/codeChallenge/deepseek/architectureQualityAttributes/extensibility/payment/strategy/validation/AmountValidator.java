package com.example.codeChallenge.deepseek.architectureQualityAttributes.extensibility.payment.strategy.validation;

import ir.dotin.extensibility.payment.PaymentRequest;

public class AmountValidator implements Validator {
    @Override
    public ValidationEnum getType() {
        return ValidationEnum.AMOUNT;
    }

    @Override
    public void validate(PaymentRequest paymentRequest) {
        if (paymentRequest.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
    }
}
