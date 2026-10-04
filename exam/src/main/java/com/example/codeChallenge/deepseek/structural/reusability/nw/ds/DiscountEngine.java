package com.example.codeChallenge.deepseek.structural.reusability.nw.ds;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiscountEngine {
    private final Map<String, Product> productPriceMap = new ConcurrentHashMap<>();
    private final Map<String, PriceDiscountCalculator> productDiscountMap = new ConcurrentHashMap<>();


    public void addProduct(Product product) {
        productPriceMap.putIfAbsent(product.id(), product);


        PriceDiscountCalculator fi = new PriceDiscountCalculator() {
            @Override
            public double calcDiscount(Product product) {
                return product.price() > 100 ? 10.0 : 5.0;
            }
        };
        productDiscountMap.put(product.id(), fi);

    }


}
