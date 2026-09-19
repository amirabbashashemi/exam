package com.example.codeChallenge.deepseek.core.availability.transaction;

public class Transaction {
    Long id;
    String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "type='" + type + '\'' +
                ", id=" + id +
                '}';
    }
}
