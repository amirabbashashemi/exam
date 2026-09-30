package com.example.codeChallenge.excercise.exam3.resilience;

public class PaymentResult  {
    Long id;
    String success;

    public PaymentResult(Long id, String success) {
        this.id = id;
        this.success = success;
    }
}
