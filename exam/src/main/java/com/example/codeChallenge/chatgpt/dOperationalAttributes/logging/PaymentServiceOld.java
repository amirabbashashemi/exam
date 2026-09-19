package com.example.codeChallenge.chatgpt.dOperationalAttributes.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentServiceOld {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceOld.class);

    public void processPayment(String paymentId, double amount) {
        LOGGER.info("Processing payment for id " + paymentId + " with amount " + amount);

        try {
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }

            // simulate processing
            if (paymentId.equals("FAIL")) {
                throw new RuntimeException("Payment processing failed");
            }

            LOGGER.info("Payment processed successfully for " + paymentId);
        } catch (Exception e) {
            LOGGER.info("Error processing payment for " + paymentId + ": " + e.getMessage());
        }
    }
}
