package com.example.codeChallenge.deepseek.structural.reusability;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscountEngine {
    // وابستگی به کاتالوگ خاص فروشگاه (Hard-Coded)
    private static final Map<String, Double> PRODUCT_PRICES = new HashMap<>();
    static {
        PRODUCT_PRICES.put("LAPTOP_DELL", 1200.0);
        PRODUCT_PRICES.put("MOUSE_HP", 25.0);
        PRODUCT_PRICES.put("KEYBOARD_LOGITECH", 80.0);
    }

    // منطق تشخیص مشتری ویژه (VIP) که فقط برای این فروشگاه است
    private static final List<String> VIP_CUSTOMERS = Arrays.asList("customer_1001", "admin_user");

    public double calculateDiscount(String customerId, String productCode) {
        Double price = PRODUCT_PRICES.get(productCode);
        if (price == null) {
            System.out.println("[ERROR] Product not found: " + productCode);
            return 0.0;
        }

        double discountPercentage = 0.0;

        // قانون تخفیف خاص فروشگاه (مختلط و Hard-Coded)
            if (VIP_CUSTOMERS.contains(customerId)) {
            discountPercentage = 20.0; // 20% تخفیف ویژه
        } else if (price > 100) {
            discountPercentage = 10.0; // 10% تخفیف برای کالاهای گران‌قیمت
        } else {
            discountPercentage = 5.0; // 5% تخفیف پایه
        }

        // چاپ لاگ! این کلاس همزمان کار محاسبه و چاپ را انجام می‌دهد
        System.out.println("[INFO] Customer: " + customerId + " | Product: " + productCode + " | Discount: " + discountPercentage + "%");
        System.out.println("[INFO] Final price: " + (price - (price * discountPercentage / 100)));

        return discountPercentage;
    }

    public static void main(String[] args) {
        DiscountEngine engine = new DiscountEngine();
        engine.calculateDiscount("customer_1001", "LAPTOP_DELL");
    }
}