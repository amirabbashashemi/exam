package com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
//Maintainability (قابلیت نگهداری)
public class PaymentService {
    private final Map<PaymentMethodEnum, PaymentStrategy> strategyMap = new ConcurrentHashMap<>();

    public PaymentService() {//find automatic
        ServiceLoader<PaymentStrategy> serviceLoader = ServiceLoader.load(PaymentStrategy.class);
        serviceLoader.forEach(paymentStrategy -> {
            strategyMap.putIfAbsent(paymentStrategy.type(), paymentStrategy);
        });
    }

    public PaymentService(Map<PaymentMethodEnum, PaymentStrategy> strategyMap) {//fill in inject
        this.strategyMap.putAll(strategyMap);
    }


    public void process(PaymentMethodEnum method, PaymentActionEnum action, double amount, String credential) {

        PaymentStrategy paymentStrategy = getValueByKeyStrategyMap(method);

        paymentStrategy.process(action, amount, credential);

    }

    public PaymentStrategy getValueByKeyStrategyMap(PaymentMethodEnum paymentMethodEnum) {
        PaymentStrategy paymentStrategy = strategyMap.get(paymentMethodEnum);
        if (paymentStrategy == null) {
            throw new IllegalArgumentException(paymentMethodEnum + " is not supported.");
        }
        return paymentStrategy;
    }
}