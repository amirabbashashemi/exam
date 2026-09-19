package com.example.codeChallenge.deepseek.structural.reusability.nw.discount;

public class ExpensiveProductDiscountStrategy implements DiscountStrategy {
    @Override
    public double calc(boolean isVIP, double price) {
        return !isVIP && price > 100 ? 10.0 : 0.0;
    }
}
