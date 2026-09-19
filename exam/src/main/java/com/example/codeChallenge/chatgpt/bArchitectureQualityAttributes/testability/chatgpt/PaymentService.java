package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.testability.chatgpt;

import codeChallenge.chatgpt.eCommon.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class PaymentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentGateway paymentGateway;
    private final boolean testMode;

    public PaymentService(PaymentGateway paymentGateway, boolean testMode) {
        this.paymentGateway = paymentGateway;
        this.testMode = testMode;
    }

    public void processPayment(User user, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        try {
            if (!testMode) {
                paymentGateway.connect();
                paymentGateway.charge(user.getCardNumber(), amount);
                paymentGateway.disconnect();
            } else {
                LOGGER.info("Test mode enabled: payment skipped for user {}", user.getId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Payment failed", e);
        }
    }
}
