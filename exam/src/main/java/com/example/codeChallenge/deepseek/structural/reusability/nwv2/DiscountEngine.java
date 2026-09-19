package com.example.codeChallenge.deepseek.structural.reusability.nwv2;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class DiscountEngine {
    private final PriceProvider priceProvider;
    private final CustomerVipProvider vipProvider;
    private final List<DiscountStrategy> strategies;
    private final Consumer<String> logger; // برای خروجی لاگ (قابل تزریق)

    // سازنده اصلی برای استفاده در کتابخانه
    public DiscountEngine(PriceProvider priceProvider,
                          CustomerVipProvider vipProvider,
                          List<DiscountStrategy> strategies,
                          Consumer<String> logger) {
        this.priceProvider = priceProvider;
        this.vipProvider = vipProvider;
        this.strategies = strategies;
        this.logger = logger != null ? logger : System.out::println; // پیش‌فرض
    }

    // سازنده‌ی ساده‌تر برای سهولت (اگر لاگر نخواهیم)
    public DiscountEngine(PriceProvider priceProvider,
                          CustomerVipProvider vipProvider,
                          List<DiscountStrategy> strategies) {
        this(priceProvider, vipProvider, strategies, System.out::println);
    }

    public double calculateBestDiscount(String customerId, String productCode) {
        // ۱. دریافت داده‌ها از تأمین‌کننده‌ها
        double price = priceProvider.getPrice(productCode);
        if (price <= 0) {
            logger.accept("[ERROR] Product not found: " + productCode);
            return 0.0;
        }

        boolean isVip = vipProvider.isVip(customerId);
        logger.accept("[INFO] Customer: " + customerId + " | Product: " + productCode + " | Price: " + price);

        // ۲. پیدا کردن بیشترین تخفیف در بین استراتژی‌ها
        double maxDiscount = strategies.stream()
                .map(strategy -> strategy.calculateDiscount(price, isVip))
                .max(Double::compare)
                .orElse(0.0);

        logger.accept("[INFO] Best Discount: " + maxDiscount + "%");
        return maxDiscount;
    }


    static void main(String[] args) {
        // ---- تأمین‌کننده‌های داده (می‌توانند از فایل، دیتابیس، REST یا هر چیزی باشند) ----
        PriceProvider inMemoryPriceProvider = productCode -> {
            Map<String, Double> db = Map.of(
                    "LAPTOP_DELL", 1200.0,
                    "MOUSE_HP", 25.0,
                    "KEYBOARD_LOGITECH", 80.0
            );
            return db.getOrDefault(productCode, 0.0);
        };

        CustomerVipProvider inMemoryVipProvider = customerId -> {
            Set<String> vipSet = Set.of("customer_1001", "admin_user");
            return vipSet.contains(customerId);
        };

        // ---- استراتژی‌های تخفیف (کاملاً مستقل از منبع داده) ----
        List<DiscountStrategy> strategies = List.of(
                new VipDiscountStrategy(),
                new ExpensiveProductDiscountStrategy(),
                new BaseDiscountStrategy()
        );

        // ---- ساخت موتور تخفیف و اجرا ----
        DiscountEngine engine = new DiscountEngine(
                inMemoryPriceProvider,
                inMemoryVipProvider,
                strategies
        );

        // تست ۱: مشتری ویژه و لپ‌تاپ
        double discount1 = engine.calculateBestDiscount("customer_1001", "LAPTOP_DELL");
        System.out.println("Final Discount: " + discount1 + "%\n");

        // تست ۲: مشتری معمولی و موس گران‌قیمت (قیمت ۲۵ <= ۱۰۰)
        double discount2 = engine.calculateBestDiscount("customer_2002", "MOUSE_HP");
        System.out.println("Final Discount: " + discount2 + "%\n");

        // تست ۳: مشتری معمولی و لپ‌تاپ (گران‌قیمت)
        double discount3 = engine.calculateBestDiscount("customer_2002", "LAPTOP_DELL");
        System.out.println("Final Discount: " + discount3 + "%");
    }
}