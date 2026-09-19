package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.extendability.strategy;

import codeChallenge.chatgpt.eCommon.Order;

import java.math.BigDecimal;

public class InternationalCalculator implements FinalPriceCalculator {
    @Override
    public BigDecimal calc(Order order, BigDecimal newPrice) {
        try {
            if (order.isInternational()) {
                return newPrice.add(BigDecimal.valueOf(20));
            } else {
                return newPrice;
            }
        } catch (Exception exception) {
            throw new RuntimeException(STR."Error in method calc in class \{this.getClass().getSimpleName()}", exception);
        }
    }
}
