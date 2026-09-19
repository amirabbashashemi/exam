package com.example.codeChallenge.deepseek.structural.reusability.nw.discount;

public class VipDiscountStrategy implements DiscountStrategy{
    @Override
    public double calc(boolean isVIP, double price) {
        return isVIP ? 20.0 : 0;
    }
}
