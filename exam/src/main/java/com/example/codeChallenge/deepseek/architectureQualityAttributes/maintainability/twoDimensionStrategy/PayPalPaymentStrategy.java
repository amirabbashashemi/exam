package com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy;

public class PayPalPaymentStrategy  extends AbstractPaymentStrategyImpl {

    @Override
    public PaymentMethodEnum type() {
        return PaymentMethodEnum.PAYPAL;
    }

    @Override
    public void authorize(double amount, String credential) {
        System.out.println("Auth PP with email: " + credential);
    }

    @Override
    public void capture(double amount) {
        System.out.println("Cap PP: " + amount);
    }

    @Override
    public void refund(double amount) {
        System.out.println("Ref PP: " + amount);
    }
}
