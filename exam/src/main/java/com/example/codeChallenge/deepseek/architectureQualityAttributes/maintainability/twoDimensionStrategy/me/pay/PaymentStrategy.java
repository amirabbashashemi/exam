package com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me.pay;

import com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me.action.PaymentActionEnum;

public interface PaymentStrategy {
    PaymentMethodEnum type();

    void handle(PaymentMethodEnum method, PaymentActionEnum action, double amount, String credential);
}
