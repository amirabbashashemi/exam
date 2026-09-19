package com.example.codeChallenge.deepseek.core.availability.order;

import java.util.*;
import java.util.concurrent.*;
/*
اگر سیستم پرداخت خارجی از کار بیفتد، همه درخواست‌ها با Timeout مواجه می‌شوند
سیستم همچنان به سیستم خارجی درخواست می‌فرستد (حتی وقتی که خراب است)
منابع سیستم هدر می‌رود (تردها منتظر Timeout می‌مانند)
زمان پاسخگویی کلی سیستم کاهش می‌یابد
هیچ مکانیزمی برای شناسایی خودکار بهبود سیستم خارجی وجود ندارد

*/
public class OrderPaymentServiceOld {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public void processOrder(Order order) {
        executor.submit(() -> {
            try {
                // ۱. اعتبارسنجی
                validateOrder(order);

                // ۲. پردازش پرداخت (سیستم خارجی)
                PaymentResult result = callExternalPaymentSystem(order);

                // ۳. به‌روزرسانی وضعیت
                if (result.isSuccess()) {
                    order.setStatus("PAID");
                    orders.put(order.getId(), order);
                } else {
                    order.setStatus("FAILED");
                }
            } catch (Exception e) {
                order.setStatus("ERROR");
                System.err.println("Error processing order: " + e.getMessage());
            }
        });
    }

    private PaymentResult callExternalPaymentSystem(Order order) {
        // شبیه‌سازی تماس با سیستم خارجی
        try {
            Thread.sleep(200); // زمان پاسخگویی معمولی
            // شبیه‌سازی خطا
            if (Math.random() > 0.7) {
                throw new RuntimeException("Payment system timeout!");
            }
            return new PaymentResult(true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new PaymentResult(false);
        }
    }

    private void validateOrder(Order order) {
        // اعتبارسنجی
    }
}