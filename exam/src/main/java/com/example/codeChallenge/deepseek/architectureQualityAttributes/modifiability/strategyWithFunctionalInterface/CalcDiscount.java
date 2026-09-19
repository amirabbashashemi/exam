package com.example.codeChallenge.deepseek.architectureQualityAttributes.modifiability.strategyWithFunctionalInterface;

@FunctionalInterface
public interface CalcDiscount {

    double calc(String userType, double amount);

}
