package com.example.codeChallenge.deepseek.architectureQualityAttributes.extendability.order.strategy.process;

import codeChallenge.deepseek.core.extensibility.order.Order;

public interface OrderProcessor {
    OrderTypeEnum getType();

    void process(Order order);
}
