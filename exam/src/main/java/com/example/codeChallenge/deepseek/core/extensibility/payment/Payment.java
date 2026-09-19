package com.example.codeChallenge.deepseek.core.extensibility.payment;

public class Payment {
    String id;
    String gateway;
    Integer amount;
    String currency;

    public Payment(String id, String gateway, Integer amount, String currency) {
        this.id = id;
        this.gateway = gateway;
        this.amount = amount;
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
