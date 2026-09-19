package com.example.codeChallenge.deepseek.architectureQualityAttributes.extendability.pay;

public class PaymentProcessor {
    public void processPayment(String method, double amount, String accountInfo) {
        if (method.equals("CREDIT_CARD")) {
            // شبیه‌سازی اتصال به درگاه کارت اعتباری
            System.out.println("Connecting to Credit Card gateway...");
            String cardNumber = accountInfo.split(",")[0];
            String cvv = accountInfo.split(",")[1];
            if (cardNumber.length() == 16 && cvv.length() == 3) {
                System.out.println("Payment of $" + amount + " processed via Credit Card.");
            } else {
                System.out.println("Credit Card validation failed.");
            }
        } else if (method.equals("PAYPAL")) {
            // شبیه‌سازی اتصال به PayPal
            System.out.println("Connecting to PayPal gateway...");
            String email = accountInfo;
            if (email.contains("@")) {
                System.out.println("Payment of $" + amount + " processed via PayPal.");
            } else {
                System.out.println("PayPal email invalid.");
            }
        } else {
            throw new IllegalArgumentException("Unsupported payment method: " + method);
        }
    }
}