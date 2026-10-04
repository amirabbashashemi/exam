package com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me.pay;

import com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me.action.ActionStrategy;
import com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me.action.PaymentActionEnum;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BasePayment {
    private final Map<PaymentActionEnum, ActionStrategy> actionEnumPaymentStrategyMap;

    public BasePayment(List<ActionStrategy> strategies) {
        actionEnumPaymentStrategyMap = new ConcurrentHashMap<>();
        for (ActionStrategy strategy : strategies) {
            actionEnumPaymentStrategyMap.put(strategy.type(), strategy);
        }
    }

    protected ActionStrategy getStrategy(PaymentActionEnum paymentActionEnum) {
        return actionEnumPaymentStrategyMap.put(paymentActionEnum);
    }

}
