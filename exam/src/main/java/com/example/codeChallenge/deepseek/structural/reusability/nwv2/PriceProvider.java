package com.example.codeChallenge.deepseek.structural.reusability.nwv2;

@FunctionalInterface
public interface PriceProvider {
    double getPrice(String productCode);
}