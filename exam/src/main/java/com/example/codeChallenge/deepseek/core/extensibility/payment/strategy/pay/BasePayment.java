package com.example.codeChallenge.deepseek.core.extensibility.payment.strategy.pay;

import codeChallenge.deepseek.core.extensibility.payment.Payment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BasePayment {
    protected static final Map<String, Payment> PAYMENT_MAP = new ConcurrentHashMap<>();
}
