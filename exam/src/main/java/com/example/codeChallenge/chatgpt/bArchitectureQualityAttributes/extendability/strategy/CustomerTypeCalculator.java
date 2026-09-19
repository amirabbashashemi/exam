package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.extendability.strategy;

import codeChallenge.chatgpt.eCommon.CustomerType;
import codeChallenge.chatgpt.eCommon.Order;

import java.math.BigDecimal;

public class CustomerTypeCalculator implements FinalPriceCalculator {
    @Override
    public BigDecimal calc(Order order, BigDecimal newPrice) {
        try {
            if (order.getCustomerType() == CustomerType.VIP) {
                return newPrice.multiply(BigDecimal.valueOf(0.9));
            } else {
                return newPrice;
            }
        } catch (Exception exception) {
            throw new RuntimeException(STR."Error in method calc in class \{this.getClass().getSimpleName()}", exception);
        }
    }

}
