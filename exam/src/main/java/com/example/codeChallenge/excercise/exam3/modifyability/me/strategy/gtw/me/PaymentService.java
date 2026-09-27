package com.example.codeChallenge.excercise.exam3.modifyability.me.strategy.gtw.me;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class PaymentService {
    List<Gateway> gateways = new ArrayList<>();

    //for increase testability
    public PaymentService() {
        ServiceLoader<Gateway> serviceLoader = ServiceLoader.load(Gateway.class);

        for (Gateway gateway : serviceLoader) {
            gateways.add(gateway);
        }
    }

    //for inject gateways
    public PaymentService(List<Gateway> gateways) {
        this.gateways = gateways;
    }

    public void pay(BankName bankName, String orderId, double amount) {
        Gateway gateway = getGateway(bankName);

        gateway.connect();
        gateway.sendPayment(orderId, amount);
        gateway.disconnect();
    }

    private Gateway getGateway(BankName forBankName) {
        return gateways.stream()
                .filter(gateway -> gateway.getBankName().equals(forBankName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Error in method getGateway. %s is invalid", forBankName)));
    }
}
