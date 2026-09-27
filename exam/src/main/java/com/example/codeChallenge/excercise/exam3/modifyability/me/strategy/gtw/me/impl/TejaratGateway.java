package com.example.codeChallenge.excercise.exam3.modifyability.me.strategy.gtw.me.impl;

import com.example.codeChallenge.excercise.exam3.modifyability.me.strategy.gtw.me.BankName;
import com.example.codeChallenge.excercise.exam3.modifyability.me.strategy.gtw.me.Gateway;

public class TejaratGateway implements Gateway {

    @Override
    public BankName getBankName() {
        return BankName.TEJERAT;
    }

    @Override
    public void connect() {
        //        simulation of connect for Tejarat

    }

    @Override
    public void sendPayment(String orderId, double amount) {
        //        simulation of sendPayment for Tejarat

    }

    @Override
    public void disconnect() {
        //        simulation of disconnect for Tejarat

    }
}
