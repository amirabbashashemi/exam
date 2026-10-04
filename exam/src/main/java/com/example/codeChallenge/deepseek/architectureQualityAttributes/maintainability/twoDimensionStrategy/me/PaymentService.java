package com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy.me;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class PaymentService {
    private final Map<PaymentStrategy> strategies;

    public PaymentService() {
        ServiceLoader<PaymentStrategy> paymentStrategies = ServiceLoader.load(PaymentStrategy.class);

        for (PaymentStrategy paymentStrategy : paymentStrategies) {

        }
    }

    public PaymentService(List<PaymentStrategy> strategies) {
        this.strategies = strategies;
    }
}
