package com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy;


public class StripePaymentStrategy extends AbstractPaymentStrategyImpl {

    @Override
    public PaymentMethodEnum type() {
        return PaymentMethodEnum.STRIPE;
    }

    @Override
    public void authorize(double amount, String credential) {
        System.out.println("Stripe authorize " + credential);

    }

    @Override
    public void capture(double amount) {
        System.out.println("Stripe capture " + amount);
    }

    @Override
    public void refund(double amount) {
        System.out.println("Stripe refund " + amount);

    }
}
