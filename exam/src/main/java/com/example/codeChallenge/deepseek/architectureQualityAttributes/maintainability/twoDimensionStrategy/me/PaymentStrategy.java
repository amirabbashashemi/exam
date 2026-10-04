package com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me;

public interface PaymentStrategy {


    void auth(double amount, String credential);

    void capture(double amount);

    void refund(double amount);
}
