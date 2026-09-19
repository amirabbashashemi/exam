package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.testability;

import java.math.BigDecimal;

public class ExternalPaymentGateway {
    public void connect() {
        // اتصال به سرور پرداخت واقعی
    }

    public void charge(String cardNumber, BigDecimal amount) {
        // پردازش تراکنش واقعی
    }

    public void disconnect() {
        // قطع اتصال
    }
}
