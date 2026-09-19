package com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability.twoDimensionStrategy;


public abstract class AbstractPaymentStrategyImpl implements PaymentStrategy {

    @Override
    public abstract PaymentMethodEnum type();

    @Override
    public void process(PaymentActionEnum action, double amount, String credential) {
        switch (action) {
            case PaymentActionEnum.AUTHORIZE -> authorize(amount, credential);
            case PaymentActionEnum.CAPTURE -> capture(amount);
            case PaymentActionEnum.REFUND -> refund(amount);
        }
    }

    protected abstract void authorize(double amount, String credential);

    protected abstract void capture(double amount);

    protected abstract void refund(double amount);

}
