package com.example.codeChallenge.deepseek.architectureQualityAttributes.extensibility.order.strategy.process;

import ir.dotin.extensibility.order.Order;

public interface OrderProcessor {
    OrderTypeEnum getType();

    void process(Order order);
}
