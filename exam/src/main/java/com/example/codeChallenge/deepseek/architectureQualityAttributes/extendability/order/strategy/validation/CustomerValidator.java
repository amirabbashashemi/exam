package com.example.codeChallenge.deepseek.architectureQualityAttributes.extendability.order.strategy.validation;

import codeChallenge.deepseek.core.extensibility.order.Order;

public class CustomerValidator implements Validator {
    @Override
    public ValidationEnum getType() {
        return ValidationEnum.CUSTOMER;
    }

    @Override
    public void validate(Order order) {
        if (order.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID required");
        }
    }
}
