package com.example.codeChallenge.excercise.exam3.resilience.me;

public class PaymentResult  {
    Long id;
    String message;

    public PaymentResult(Long id, String success) {
        this.id = id;
        this.message = success;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
