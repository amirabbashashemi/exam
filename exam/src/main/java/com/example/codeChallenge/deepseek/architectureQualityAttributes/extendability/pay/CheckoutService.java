package com.example.codeChallenge.deepseek.architectureQualityAttributes.extendability.pay;


import codeChallenge.deepseek.architectureQualityAttributes.extendability.pay.nw.PaymentProcessor;
import codeChallenge.deepseek.architectureQualityAttributes.extendability.pay.nw.CreditCardPayment;
import codeChallenge.deepseek.architectureQualityAttributes.extendability.pay.nw.PaymentProcessorService;
import codeChallenge.deepseek.architectureQualityAttributes.extendability.pay.nw.PaypalPayment;
import codeChallenge.deepseek.architectureQualityAttributes.extendability.pay.nw.ProcessorTypeEnum;

import java.util.ArrayList;
import java.util.List;

public class CheckoutService {
    static void main(String[] args) {
        PaymentProcessorService paymentProcessorService;
        String injectionType = System.getenv("injection-type");

        if ("constructor".equals(injectionType)) {
            List<PaymentProcessor> paymentProcessors = new ArrayList<>();
            PaymentProcessor creditCardPayment = new CreditCardPayment();
            PaymentProcessor paypalPayment = new PaypalPayment();

            paymentProcessors.add(creditCardPayment);
            paymentProcessors.add(paypalPayment);

            paymentProcessorService = new PaymentProcessorService(paymentProcessors);
        } else {
            paymentProcessorService = new PaymentProcessorService();
        }

        // پرداخت با کارت اعتباری
        paymentProcessorService.process(ProcessorTypeEnum.CREDIT_CARD, 250.0, "1234567890123456,123");

        // پرداخت با پی‌پال
        paymentProcessorService.process(ProcessorTypeEnum.PAYPAL, 150.0, "user@paypal.com");

    }
}