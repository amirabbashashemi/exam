package com.example.codeChallenge.deepseek.architectureQualityAttributes.interoperability.nw.strategy;

import codeChallenge.deepseek.architectureQualityAttributes.interoperability.nw.PaymentTransaction;

public class PaymentUnsupportedFormatter implements PaymentFormatter {
    @Override
    public PaymentMethodEnum type() {
        return PaymentMethodEnum.UNSUPPORTED;
    }

    @Override
    public String format(PaymentTransaction paymentTransaction) {
       return "Unsupported format transaction id : " + paymentTransaction.getId();
    }
}
