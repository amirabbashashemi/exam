package com.example.codeChallenge.deepseek.architectureQualityAttributes.extensibility.order.strategy.validation;

import ir.dotin.extensibility.order.Order;

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
