package com.example.codeChallenge.deepseek.architectureQualityAttributes.extendability.pay.nw;

public class CreditCardPayment implements PaymentProcessor {
    @Override
    public ProcessorTypeEnum getType() {
        return ProcessorTypeEnum.CREDIT_CARD;
    }

    @Override
    public void process(double amount, String accountInfo) {
        simulateProcess(amount, accountInfo);
    }

    // من این متد رو تغییر نمدم که منطق سوال خراب نشه
    // شبیه‌سازی اتصال به درگاه کارت اعتباری
    private void simulateProcess(double amount, String accountInfo) {
        System.out.println("Connecting to Credit Card gateway...");
        String cardNumber = accountInfo.split(",")[0];
        String cvv = accountInfo.split(",")[1];
        if (cardNumber.length() == 16 && cvv.length() == 3) {
            System.out.println("Payment of $" + amount + " processed via Credit Card.");
        } else {
            System.out.println("Credit Card validation failed.");
        }
    }
}
