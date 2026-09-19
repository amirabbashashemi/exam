package com.example.codeChallenge.deepseek.structural.reusability.nw.discount;

public class BaseDiscountStrategy implements DiscountStrategy {
    @Override
    public double calc(boolean isVIP, double price) {
        return 5.0;
    }
}

