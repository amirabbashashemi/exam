package com.example.codeChallenge.deepseek.structural.reusability.nwv2;

public class BaseDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculateDiscount(double price, boolean isVip) {
        // اگر هیچ قانون دیگری اعمال نشد، ۵٪ تخفیف پایه
        return 5.0;
    }
}