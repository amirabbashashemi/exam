package com.example.codeChallenge.deepseek.structural.reusability.nw;

import codeChallenge.deepseek.structural.reusability.nwv2.BaseDiscountStrategy;
import codeChallenge.deepseek.structural.reusability.nwv2.DiscountStrategy;
import codeChallenge.deepseek.structural.reusability.nwv2.ExpensiveProductDiscountStrategy;
import codeChallenge.deepseek.structural.reusability.nwv2.VipDiscountStrategy;

import java.util.*;

public class DiscountEngine {
    private final PriceProvider priceProvider;
    private final CustomerProvider customerProvider;
    private final List<DiscountStrategy> discountStrategies;

    public DiscountEngine(PriceProvider priceProvider, CustomerProvider customerProvider, List<DiscountStrategy> discountStrategies) {
        this.priceProvider = priceProvider;
        this.customerProvider = customerProvider;
        this.discountStrategies = discountStrategies;
    }

    public double calculateBestDiscount(String customerId, String product) {
        double price = priceProvider.getPrice(product);
        if (price <= 0) {
            System.err.printf("[ERROR] Product not found: %s", product);
            return 0.0;
        }

        boolean isVip = customerProvider.isVIP(customerId);

        return discountStrategies.stream()
                .map(strategy -> strategy.calc(isVip, price))
                .max(Double::compare)
                .orElse(0.0);
    }


    static void main() {
        PriceProvider priceProvider = product -> getPrice().get(product);
        CustomerProvider customerProvider = customerId -> getVIPCustomer(customerId);
        List<DiscountStrategy> discountStrategieList = new ArrayList<>();
        discountStrategieList.add(new BaseDiscountStrategy());
        discountStrategieList.add(new ExpensiveProductDiscountStrategy());
        discountStrategieList.add(new VipDiscountStrategy());

        DiscountEngine engine = new DiscountEngine(priceProvider, customerProvider, discountStrategieList);

        // تست ۱: مشتری ویژه و لپ‌تاپ
        double discount1 = engine.calculateBestDiscount("customer_1001", "LAPTOP_DELL");
        System.out.println("Final Discount: " + discount1 + "%\n");

    }

    private static boolean getVIPCustomer(String customer) {
        //from db
        //from rest

        Set<String> set = new HashSet<>();
        set.add("customer_1001");
        set.add("admin_user");

        return set.contains(customer);
    }

    private static Map<String, Double> getPrice() {
        //from db
        //from rest

        Map<String, Double> map = Map.of(
                "LAPTOP_DELL", 1200.0,
                "MOUSE_HP", 25.0,
                "KEYBOARD_LOGITECH", 80.0
        );

        return map;
    }
}
