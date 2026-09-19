package com.example.codeChallenge.deepseek.architectureQualityAttributes.extensibility.order;

import ir.dotin.extensibility.order.strategy.process.OrderProcessorService;
import ir.dotin.extensibility.order.strategy.validation.ValidationEnum;
import ir.dotin.extensibility.order.strategy.validation.ValidatorService;

import java.util.*;
/*
مشکلات فعلی:
هر بار که نوع سفارش جدید اضافه می‌شود، باید کد اصلی را تغییر دهیم (نقض اصل Open/Closed)
کد processOrder بزرگ و غیرقابل مدیریت شده است (بیش از ۵۰ خط)
منطق‌های مختلف در هم آمیخته شده‌اند (اعتبارسنجی، تخفیف، نوتیفیکیشن، ذخیره‌سازی)
تست کردن هر نوع سفارش به صورت جداگانه سخت است
توسعه‌دهندگان جدید باید کلاس را کامل درک کنند تا تغییر ایجاد کنند
 */
public class OrderProcessor {
    private final List<Order> orders = new ArrayList<>();
    private static final ValidatorService VALIDATOR_SERVICE = new ValidatorService();
    private static final OrderProcessorService ORDER_PROCESSOR_SERVICE = new OrderProcessorService();

    public void processOrder(Order order) {
        // اعتبارسنجی عمومی
        VALIDATOR_SERVICE.validate(order, ValidationEnum.TOTAL);

        VALIDATOR_SERVICE.validate(order, ValidationEnum.CUSTOMER);

        ORDER_PROCESSOR_SERVICE.processOrder(order);
    }
}