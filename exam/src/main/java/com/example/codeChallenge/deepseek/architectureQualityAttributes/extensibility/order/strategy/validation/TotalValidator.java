package com.example.codeChallenge.deepseek.architectureQualityAttributes.extensibility.order.strategy.validation;

import ir.dotin.extensibility.order.Order;

public class TotalValidator implements Validator {
    @Override
    public ValidationEnum getType() {
        return ValidationEnum.TOTAL;
    }

    @Override
    public void validate(Order order) {
        if (order.getTotal() < 0) {
            throw new IllegalArgumentException("Invalid total");
        }
    }
}
