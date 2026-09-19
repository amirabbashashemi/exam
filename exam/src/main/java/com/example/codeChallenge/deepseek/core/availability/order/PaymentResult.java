package com.example.codeChallenge.deepseek.core.availability.order;

public class PaymentResult {
    boolean success;

    public PaymentResult(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
