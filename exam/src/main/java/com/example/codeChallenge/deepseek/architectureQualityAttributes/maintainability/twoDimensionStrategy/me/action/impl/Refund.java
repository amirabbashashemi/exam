package com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me.action.impl;

import com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me.action.ActionStrategy;
import com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me.action.PaymentActionEnum;

public class Refund implements ActionStrategy {

    @Override
    public PaymentActionEnum type() {
        return PaymentActionEnum.REFUND;
    }

    @Override
    public void handle(Object... objects) {
        double amount = (double) objects[0];

        System.out.println("Ref CC: " + amount);
        // منطق سنگین بازگشت وجه
    }
}
