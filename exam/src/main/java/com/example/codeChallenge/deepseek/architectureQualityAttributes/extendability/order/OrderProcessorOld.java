package com.example.codeChallenge.deepseek.architectureQualityAttributes.extendability.order;

import java.util.ArrayList;
import java.util.List;

/*
مشکلات فعلی:
هر بار که نوع سفارش جدید اضافه می‌شود، باید کد اصلی را تغییر دهیم (نقض اصل Open/Closed)
کد processOrder بزرگ و غیرقابل مدیریت شده است (بیش از ۵۰ خط)
منطق‌های مختلف در هم آمیخته شده‌اند (اعتبارسنجی، تخفیف، نوتیفیکیشن، ذخیره‌سازی)
تست کردن هر نوع سفارش به صورت جداگانه سخت است
توسعه‌دهندگان جدید باید کلاس را کامل درک کنند تا تغییر ایجاد کنند
 */
public class OrderProcessorOld {
    private final List<Order> orders = new ArrayList<>();

    public void processOrder(Order order) {
        // اعتبارسنجی عمومی
        if (order.getTotal() < 0) {
            throw new IllegalArgumentException("Invalid total");
        }
        if (order.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID required");
        }

        // پردازش بر اساس نوع سفارش
        if ("REGULAR".equals(order.getType())) {
            // پردازش سفارش معمولی
            System.out.println("Processing regular order: " + order.getId());
            applyRegularDiscount(order);
            saveOrder(order);

        } else if ("VIP".equals(order.getType())) {
            // پردازش سفارش ویژه
            System.out.println("Processing VIP order: " + order.getId());
            applyVIPDiscount(order);
            sendVIPNotification(order);
            saveOrder(order);

        } else if ("INTERNATIONAL".equals(order.getType())) {
            // پردازش سفارش بین‌المللی
            System.out.println("Processing international order: " + order.getId());
            applyInternationalDiscount(order);
            validateInternationalShipping(order);
            saveOrder(order);
            sendInternationalNotification(order);

        } else {
            throw new IllegalArgumentException("Unknown order type: " + order.getType());
        }

        // ثبت تاریخچه
        orders.add(order);
    }

    private void applyRegularDiscount(Order order) {
        order.setDiscount(order.getTotal() * 0.05);
    }

    private void applyVIPDiscount(Order order) {
        order.setDiscount(order.getTotal() * 0.15);
    }

    private void applyInternationalDiscount(Order order) {
        order.setDiscount(order.getTotal() * 0.10);
    }

    private void sendVIPNotification(Order order) {
        System.out.println("VIP notification sent for order: " + order.getId());
    }

    private void sendInternationalNotification(Order order) {
        System.out.println("International notification sent for order: " + order.getId());
    }

    private void validateInternationalShipping(Order order) {
        System.out.println("International shipping validated for order: " + order.getId());
    }

    private void saveOrder(Order order) {
        System.out.println("Order saved: " + order.getId());
    }
}