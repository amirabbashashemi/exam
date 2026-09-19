package com.example.codeChallenge.deepseek.structural.reusability.nwv2;

public class VipDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculateDiscount(double price, boolean isVip) {
        if (isVip) {
            return 20.0; // 20% تخفیف
        }
        return 0.0;
    }
}