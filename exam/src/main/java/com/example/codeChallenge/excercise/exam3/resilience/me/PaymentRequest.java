package com.example.codeChallenge.excercise.exam3.resilience.me;

public class PaymentRequest {
    private Long id;
    private int tryCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTryCount() {
        return tryCount;
    }

    public void setTryCount(int tryCount) {
        this.tryCount = tryCount;
    }
}
