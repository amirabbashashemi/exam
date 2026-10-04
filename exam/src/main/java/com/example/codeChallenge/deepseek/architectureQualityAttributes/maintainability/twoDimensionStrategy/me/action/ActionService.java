package com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me.action;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActionService {
    private final Map<PaymentActionEnum, ActionStrategy> actionStrategyMap;

    public ActionService(List<ActionStrategy> actionStrategies) {
        actionStrategyMap = new ConcurrentHashMap<>();

        for (ActionStrategy actionStrategy : actionStrategies) {
            this.actionStrategyMap.put(actionStrategy.type(), actionStrategy);
        }
    }

    public void handle(PaymentActionEnum paymentActionEnum, double amount, String credential) {
        ActionStrategy actionStrategy = actionStrategyMap.get(paymentActionEnum);
        actionStrategy.handle(amount, credential);
    }

}
