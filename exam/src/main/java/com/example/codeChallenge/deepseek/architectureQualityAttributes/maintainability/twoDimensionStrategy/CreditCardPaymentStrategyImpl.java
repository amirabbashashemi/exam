package com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy;

import excercise.exem3.architectureQualityAttributes.maintainability.strategy.PaymentMethodEnum;

public class CreditCardPaymentStrategyImpl extends AbstractPaymentStrategyImpl {

    @Override
    public PaymentMethodEnum type() {
        return PaymentMethodEnum.CREDIT_CARD;
    }

    @Override
    public void authorize(double amount, String credential) {
        System.out.println("Auth CC: " + credential + " for " + amount);
        // منطق سنگین احراز کارت
    }

    @Override
    public void capture(double amount) {
        System.out.println("Cap CC: " + amount);
        // منطق سنگین تسویه
    }

    @Override
    public void refund(double amount) {
        System.out.println("Ref CC: " + amount);
        // منطق سنگین بازگشت وجه
    }
}
