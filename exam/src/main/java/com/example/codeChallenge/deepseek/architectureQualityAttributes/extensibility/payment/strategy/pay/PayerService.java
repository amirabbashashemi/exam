package com.example.codeChallenge.deepseek.architectureQualityAttributes.extensibility.payment.strategy.pay;

import ir.dotin.extensibility.payment.PaymentRequest;

import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class PayerService {
    private final ServiceLoader<Payer> serviceLoaders = ServiceLoader.load(Payer.class);
    private static final Map<String, Payer> PAYER_MAP = new ConcurrentHashMap<>();

    public PayerService() {
        serviceLoaders.forEach(payer -> PAYER_MAP.put(payer.getType().name(), payer));
    }

    public void pay(PaymentRequest paymentRequest) {
        Payer payer = getPayer(paymentRequest.getGateway());

        payer.pay(paymentRequest);
    }

    private Payer getPayer(String payEnum) {
        Payer payer = PAYER_MAP.get(payEnum);

        if (Objects.isNull(payer)) {
            throw new IllegalArgumentException("Unknown gateway: " + payEnum);
        }

        return payer;
    }

}
