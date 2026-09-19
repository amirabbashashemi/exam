package com.example.codeChallenge.deepseek.structural.reusability.nwv2;

public class ExpensiveProductDiscountStrategy implements DiscountStrategy {
    private static final double THRESHOLD = 100.0;

    @Override
    public double calculateDiscount(double price, boolean isVip) {
        // حتی اگر کاربر VIP باشد، این قانون برای کالاهای گران اعمال می‌شود
        // (می‌توانیم ترکیب کنیم یا بالاترین را انتخاب کنیم)
        if (price > THRESHOLD && !isVip) { // فقط برای غیرVIP ها
            return 10.0;
        }
        return 0.0;
    }
}