package com.example.codeChallenge.excercise.exam3.modifyability.me.strategy.gtw;

public class PaymentService {

    public void pay(String orderId, double amount) {

        MellatGateway gateway = new MellatGateway();
        gateway.connect();
        gateway.sendPayment(orderId, amount);
        gateway.disconnect();

    }

}
