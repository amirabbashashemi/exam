package com.example.codeChallenge.deepseek.architectureQualityAttributes.extensibility.payment.strategy.pay;

import ir.dotin.extensibility.payment.Payment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BasePayment {
    protected static final Map<String, Payment> PAYMENT_MAP = new ConcurrentHashMap<>();
}
