package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.testability.chatgpt;

import java.math.BigDecimal;

public interface PaymentGateway {
    void connect();

    void charge(String cardNumber, BigDecimal amount);

    void disconnect();

}
