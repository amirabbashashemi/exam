package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.testability.chatgpt;

import java.math.BigDecimal;

public class ExternalPaymentGateway implements PaymentGateway{
    @Override
    public void connect() {
        // اتصال به سرور پرداخت واقعی
    }

    @Override
    public void charge(String cardNumber, BigDecimal amount) {
        // پردازش تراکنش واقعی
    }

    @Override
    public void disconnect() {
        // قطع اتصال
    }
}
