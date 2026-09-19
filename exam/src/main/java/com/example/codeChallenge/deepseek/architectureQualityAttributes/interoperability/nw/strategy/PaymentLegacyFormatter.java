package com.example.codeChallenge.deepseek.architectureQualityAttributes.interoperability.nw.strategy;

import codeChallenge.deepseek.architectureQualityAttributes.interoperability.nw.PaymentTransaction;

public class PaymentLegacyFormatter implements PaymentFormatter {
    private static final String discriminator = "|";

    @Override
    public PaymentMethodEnum type() {
        return PaymentMethodEnum.LEGACY;
    }

    @Override
    public String format(PaymentTransaction paymentTransaction) {
        StringBuilder stringBuilder = new StringBuilder(paymentTransaction.getId())
                .append(discriminator)
                .append(paymentTransaction.getAmount())
                .append(discriminator)
                .append(paymentTransaction.getCurrency())
                .append(discriminator)
                .append(paymentTransaction.getDescription());

        return stringBuilder.toString();
    }
}
