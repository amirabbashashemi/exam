package com.example.codeChallenge.deepseek.architectureQualityAttributes.extensibility.order.strategy.process;

import ir.dotin.extensibility.order.Order;

public class BaseOrderProcessor {

    protected void saveOrder(Order order) {
        System.out.println("Order saved: " + order.getId());
    }

}
