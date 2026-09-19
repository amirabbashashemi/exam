package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.extendability;

import codeChallenge.chatgpt.eCommon.CustomerType;
import codeChallenge.chatgpt.eCommon.Order;

import java.math.BigDecimal;

public class Extendability {

    public BigDecimal calculateFinalPrice(Order order) {
        BigDecimal price = order.getBasePrice();

        if (order.getCustomerType() == CustomerType.VIP) {
            price = price.multiply(BigDecimal.valueOf(0.9));
        }

        if (order.isInternational()) {
            price = price.add(BigDecimal.valueOf(20));
        }

        if (order.getItemsCount() > 10) {
            price = price.multiply(BigDecimal.valueOf(0.95));
        }

        return price;
    }

}
