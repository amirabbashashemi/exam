package com.example.codeChallenge.deepseek.architectureQualityAttributes.interoperability.nw.strategy.impl;

import codeChallenge.deepseek.architectureQualityAttributes.interoperability.nw.PaymentTransaction;
import com.example.codeChallenge.deepseek.architectureQualityAttributes.interoperability.nw.strategy.PaymentFormatter;
import com.example.codeChallenge.deepseek.architectureQualityAttributes.interoperability.nw.strategy.PaymentMethodEnum;

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
