package com.example.codeChallenge.deepseek.architectureQualityAttributes.extendability.pay.nw;

public class PaypalPayment implements PaymentProcessor {
    @Override
    public ProcessorTypeEnum getType() {
        return ProcessorTypeEnum.PAYPAL;
    }

    @Override
    public void process(double amount, String accountInfo) {
        simulateProcess(amount, accountInfo);
    }

    // من این متد رو تغییر نمدم که منطق سوال خراب نشه
    // شبیه‌سازی اتصال به PayPal
    private void simulateProcess(double amount, String accountInfo) {
        System.out.println("Connecting to PayPal gateway...");
        String email = accountInfo;
        if (email.contains("@")) {
            System.out.println("Payment of $" + amount + " processed via PayPal.");
        } else {
            System.out.println("PayPal email invalid.");
        }
    }
}
