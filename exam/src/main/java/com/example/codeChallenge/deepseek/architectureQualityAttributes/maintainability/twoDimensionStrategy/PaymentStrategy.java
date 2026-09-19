package com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy;

public interface PaymentStrategy {

    PaymentMethodEnum type();

    void process(PaymentActionEnum action, double amount, String credential);

}
