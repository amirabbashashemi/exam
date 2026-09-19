package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.testability;

import codeChallenge.chatgpt.eCommon.User;

import java.math.BigDecimal;

public class PaymentServiceOld {
    private final ExternalPaymentGateway gateway = new ExternalPaymentGateway();

    public void processPayment(User user, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        try {
            // اتصال به سرویس خارجی
            gateway.connect();
            gateway.charge(user.getCardNumber(), amount);
            gateway.disconnect();
        } catch (Exception e) {
            throw new RuntimeException("Payment failed", e);
        }
    }
}

