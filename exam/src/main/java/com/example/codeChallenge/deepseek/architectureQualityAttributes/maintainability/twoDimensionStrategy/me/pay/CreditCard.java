package com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me.pay;

import com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me.action.PaymentActionEnum;

import java.util.Map;

public class CreditCard implements PaymentStrategy {

    @Override
    public PaymentMethodEnum type() {
        return PaymentMethodEnum.CREDIT_CARD;
    }

    @Override
    public void handle(PaymentMethodEnum method, PaymentActionEnum action, double amount, String credential) {

    }
}
