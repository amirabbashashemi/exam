package com.example.codeChallenge.deepseek.architectureQualityAttributes.extendability.order.strategy.process;

import codeChallenge.deepseek.core.extensibility.order.Order;

public class BaseOrderProcessor {

    protected void saveOrder(Order order) {
        System.out.println("Order saved: " + order.getId());
    }

}
