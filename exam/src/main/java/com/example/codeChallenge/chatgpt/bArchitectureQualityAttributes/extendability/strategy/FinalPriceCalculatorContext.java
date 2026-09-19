package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.extendability.strategy;

import codeChallenge.chatgpt.eCommon.Order;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class FinalPriceCalculatorContext {
    private final List<FinalPriceCalculator> registeredPriceList = new LinkedList<>();

    public void registerPriceCalculator(FinalPriceCalculator finalPriceCalculator) {
        registeredPriceList.add(finalPriceCalculator);
    }

    public BigDecimal calculateFinalPrice(Order order) {
        try {
            BigDecimal finalPrice = order.getBasePrice();

            for (FinalPriceCalculator finalPriceCalculator : registeredPriceList) {
                finalPrice = finalPriceCalculator.calc(order, finalPrice);
            }

            return finalPrice;
        } catch (Exception exception) {
            throw new RuntimeException("Error in method calculateFinalPrice in class " + this.getClass().getSimpleName(),
                    exception);
        }
    }
}
