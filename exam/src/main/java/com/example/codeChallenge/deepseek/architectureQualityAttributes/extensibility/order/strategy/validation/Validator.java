package com.example.codeChallenge.deepseek.architectureQualityAttributes.extensibility.order.strategy.validation;

import ir.dotin.extensibility.order.Order;

public interface Validator {
    ValidationEnum getType();

    void validate(Order order);
}
