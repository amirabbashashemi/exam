package com.example.codeChallenge.deepseek.architectureQualityAttributes.extendability.order.strategy.validation;

import codeChallenge.deepseek.core.extensibility.order.Order;

public interface Validator {
    ValidationEnum getType();

    void validate(Order order);
}
