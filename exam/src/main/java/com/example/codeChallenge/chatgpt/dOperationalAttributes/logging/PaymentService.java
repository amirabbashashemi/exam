package com.example.codeChallenge.chatgpt.dOperationalAttributes.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    public void processPayment(String paymentId, double amount) {
        LOGGER.info("Processing payment for id {} with amount {}", paymentId, amount);

        try {
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }

            // simulate processing
            if (paymentId.equals("FAIL")) {
                throw new RuntimeException("Payment processing failed");
            }

            LOGGER.info("Payment processed successfully for {}", paymentId);
        } catch (Exception exception) {
            LOGGER.error("An exception occurred for paymentId {}. exception {}", paymentId, exception.getMessage(), exception);
        }
    }
}
