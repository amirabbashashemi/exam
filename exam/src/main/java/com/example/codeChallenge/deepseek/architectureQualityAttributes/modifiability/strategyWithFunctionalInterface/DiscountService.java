package com.example.codeChallenge.deepseek.architectureQualityAttributes.modifiability.strategyWithFunctionalInterface;

import java.util.HashMap;
import java.util.Map;

public class DiscountService {
    private final Map<String, CalcDiscount> userCalcMap;

    public DiscountService(Map<String, CalcDiscount> userCalcMap) {
        this.userCalcMap = userCalcMap;
    }

    public void registerUserRateMap(Map<String, CalcDiscount> inputMap) {
        userCalcMap.clear();
        userCalcMap.putAll(inputMap);
    }

    public void addUserRateMap(String userType, double rate) {
        CalcDiscount calcDiscount = (ut, amount) -> amount * rate;

        userCalcMap.put(userType, calcDiscount);
    }

    public double calculate(String userType, double amount) {
        CalcDiscount calcDiscount = userCalcMap.get(userType);

        return calcDiscount != null ? calcDiscount.calc(userType, amount) : amount;
    }

    public static void main(String[] args) {
        Map<String, CalcDiscount> map = new HashMap<>();

        map.put("VIP", new CalcDiscount() {
            @Override
            public double calc(String userType, double amount) {
                return amount * 0.8;
            }
        });

        map.put("Employee", new CalcDiscount() {
            @Override
            public double calc(String userType, double amount) {
                return amount * 0.85;
            }
        });

        map.put("Regular", new CalcDiscount() {
            @Override
            public double calc(String userType, double amount) {
                return amount * 0.95;
            }
        });

        DiscountService discountService = new DiscountService(map);
        discountService.addUserRateMap("Peronel", 0.90);

        System.out.println(discountService.calculate("VIP", 10000));
        System.out.println(discountService.calculate("Employee", 10000));
        System.out.println(discountService.calculate("Peronel", 10000));
        System.out.println(discountService.calculate("Regular", 10000));
    }
}
