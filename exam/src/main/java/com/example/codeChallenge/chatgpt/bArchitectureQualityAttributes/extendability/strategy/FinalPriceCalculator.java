package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.extendability.strategy;

import codeChallenge.chatgpt.eCommon.Order;

import java.math.BigDecimal;

public interface FinalPriceCalculator {

    BigDecimal calc(Order order, BigDecimal newPrice);

}
