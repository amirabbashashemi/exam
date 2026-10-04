package com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me.action.impl;

import com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me.action.ActionStrategy;
import com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me.action.PaymentActionEnum;

public class Authorize implements ActionStrategy {

    @Override
    public PaymentActionEnum type() {
        return PaymentActionEnum.AUTHORIZE;
    }

    @Override
    public void handle(Object... objects) {
        double amount = (double) objects[0];
        String credential = objects[1].toString();
        System.out.println("Auth CC: " + credential + " for " + amount);
        // منطق سنگین احراز کارت
    }
}
