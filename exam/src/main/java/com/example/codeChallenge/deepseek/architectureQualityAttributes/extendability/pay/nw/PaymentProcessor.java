package com.example.codeChallenge.deepseek.architectureQualityAttributes.extendability.pay.nw;

public interface PaymentProcessor {
    ProcessorTypeEnum getType();

    void process(double amount, String accountInfo);
}
