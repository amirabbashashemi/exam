package com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me.action;

public interface ActionStrategy {
    PaymentActionEnum type();

    void handle(Object... objects);
}
