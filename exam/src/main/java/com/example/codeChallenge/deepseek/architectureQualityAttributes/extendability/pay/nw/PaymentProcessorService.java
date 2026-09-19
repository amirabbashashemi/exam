package com.example.codeChallenge.deepseek.architectureQualityAttributes.extendability.pay.nw;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentProcessorService {
    private final Map<ProcessorTypeEnum, PaymentProcessor> paymentProcessorMap = new ConcurrentHashMap<>();

    //اگر ور ورودی پر نبود با serviceLoader لود کن
    public PaymentProcessorService() {
        List<PaymentProcessor> paymentProcessors = new ArrayList<>();
        ServiceLoader<PaymentProcessor> paymentProcessorServiceLoader = ServiceLoader.load(PaymentProcessor.class);
        paymentProcessorServiceLoader.forEach(paymentProcessors::add);
        initiateMap(paymentProcessors);
    }

    //اگر paymentProcessors در ورودی پر شده بود
    public PaymentProcessorService(List<PaymentProcessor> paymentProcessors) {
        initiateMap(paymentProcessors);
    }

    public void process(ProcessorTypeEnum processorTypeEnum, double amount, String accountInfo) {
        PaymentProcessor paymentProcessor = getPaymentProcessor(processorTypeEnum);
        paymentProcessor.process(amount, accountInfo);
    }

    private void initiateMap(List<PaymentProcessor> paymentProcessors) {
        paymentProcessors.forEach(paymentProcessor -> paymentProcessorMap.putIfAbsent(paymentProcessor.getType(), paymentProcessor));
    }

    private PaymentProcessor getPaymentProcessor(ProcessorTypeEnum processorTypeEnum) {
        PaymentProcessor paymentProcessor = paymentProcessorMap.get(processorTypeEnum);

        if (paymentProcessor == null) {
            String errorMessage = String.format("Unsupported payment method: %s", processorTypeEnum.name());
            System.err.println(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        return paymentProcessor;
    }

}
