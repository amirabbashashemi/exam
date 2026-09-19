package com.example.codeChallenge.deepseek.structural.reusability.nwv2;

@FunctionalInterface
public interface DiscountStrategy {
    // فقط منطق تخفیف را محاسبه می‌کند، هیچ ایده‌ای از منبع داده ندارد
    double calculateDiscount(double price, boolean isVip);
}