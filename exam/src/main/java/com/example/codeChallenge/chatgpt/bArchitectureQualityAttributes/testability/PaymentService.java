package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.testability;

import codeChallenge.chatgpt.eCommon.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PaymentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);
    private final ExternalPaymentGateway gateway = new ExternalPaymentGateway();

    public void processPayment(User user, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        try {
            if (!isTestMode()) {
                // اتصال به سرویس خارجی
                gateway.connect();
                gateway.charge(user.getCardNumber(), amount);
                gateway.disconnect();
            }
        } catch (Exception e) {
            throw new RuntimeException("Payment failed", e);
        }
    }

    private boolean isTestMode() {
        boolean isTestMode = false;
        try {
            URI uri = this.getClass().getResource("app-test.config").toURI();
            Path path = Paths.get(uri);
            if (Files.exists(path)) {
                List<String> strings = Files.readAllLines(path);
                String configProperty = strings.stream()
                        .filter(item -> item.equalsIgnoreCase("test-mode"))
                        .distinct()
                        .findFirst()
                        .orElse(null);

                String[] arrayString = configProperty.split("=");
                if (arrayString.length == 2) {
                    String property = arrayString[1];
                    if (property.equalsIgnoreCase("true")) {
                        isTestMode = true;
                    }
                }
            } else {
                LOGGER.error("File 'app-test.config' is not exists");
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return isTestMode;
    }
}

