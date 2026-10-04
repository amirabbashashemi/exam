package com.example.codeChallenge.deepseek.architectureQualityAttributes.interoperability.nw.strategy;

import com.example.codeChallenge.deepseek.architectureQualityAttributes.interoperability.nw.PaymentTransaction;

public interface PaymentFormatter {
    PaymentMethodEnum type();

    String format(PaymentTransaction paymentTransaction);
}
