package com.example.codeChallenge.deepseek.core.resilience.pay;

public class Payment {
    String id;
    String status;

    public Payment(String id) {


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
