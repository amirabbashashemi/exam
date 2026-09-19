package com.example.codeChallenge.deepseek.architectureQualityAttributes.interoperability.nw.strategy;


import codeChallenge.deepseek.architectureQualityAttributes.interoperability.nw.PaymentTransaction;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentFormatterService {
    private final Map<PaymentMethodEnum, PaymentFormatter> formatterMath = new ConcurrentHashMap<>();

    public PaymentFormatterService() {
        ServiceLoader<PaymentFormatter> serviceLoader = ServiceLoader.load(PaymentFormatter.class);

        serviceLoader.forEach(paymentFormatter -> formatterMath.put(paymentFormatter.type(), paymentFormatter));
    }

    public void register(PaymentMethodEnum paymentMethodEnum, PaymentFormatter paymentFormatter) {
        formatterMath.put(paymentMethodEnum, paymentFormatter);
        System.out.printf("paymentFormatter %s added", paymentMethodEnum.name());

    }

    public String format(PaymentMethodEnum PaymentMethodEnum, PaymentTransaction paymentTransaction) {
        PaymentFormatter paymentFormatter = getPaymentFormatter(PaymentMethodEnum);

        return paymentFormatter.format(paymentTransaction);
    }

    private PaymentFormatter getPaymentFormatter(PaymentMethodEnum paymentMethodEnum) {
        PaymentFormatter paymentFormatter = formatterMath.get(paymentMethodEnum);
        if (paymentFormatter == null) {
            System.err.printf("paymentFormatter %s not found", paymentMethodEnum.name());
            PaymentMethodEnum unsupported = PaymentMethodEnum.UNSUPPORTED;
            paymentFormatter = formatterMath.get(unsupported);
        }
        return paymentFormatter;
    }
}
