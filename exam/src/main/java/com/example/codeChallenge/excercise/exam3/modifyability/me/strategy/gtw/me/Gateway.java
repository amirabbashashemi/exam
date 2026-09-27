package com.example.codeChallenge.excercise.exam3.modifyability.me.strategy.gtw.me;

public interface Gateway {
    BankName getBankName();

    void connect();

    void sendPayment(String orderId, double amount);

    void disconnect();
}
