package com.example.codeChallenge.deepseek.core.performance.order;

import java.util.ArrayList;
import java.util.List;

public class OrderServiceOld {
        private final List<Order> orders = new ArrayList<>();

        public void processOrders() {
            // دریافت سفارشات از یک منبع داده
            List<Order> newOrders = fetchOrdersFromQueue();

            // تبدیل و اعتبارسنجی
            for (Order order : newOrders) {
                // اعتبارسنجی محصولات
                for (Product product : order.getProducts()) {
                    if (product.getPrice() < 0) {
                        throw new IllegalArgumentException("Invalid price");
                    }
                    if (product.getName() == null || product.getName().trim().isEmpty()) {
                        throw new IllegalArgumentException("Invalid name");
                    }
                    // اعتبارسنجی بیشتر...
                }

                // محاسبه تخفیف
                double discount = 0;
                if (order.getTotal() > 1000) {
                    discount = order.getTotal() * 0.1;
                }
                if (order.getTotal() > 5000) {
                    discount = order.getTotal() * 0.2;
                }
                if (order.getTotal() > 10000) {
                    discount = order.getTotal() * 0.3;
                }

                // ذخیره در دیتابیس (شبیه‌سازی)
                saveToDatabase(order);
                orders.add(order);
            }
        }

        private List<Order> fetchOrdersFromQueue() {
            // شبیه‌سازی دریافت ۱۰۰۰۰ سفارش
            List<Order> result = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                result.add(generateOrder(i));
            }
            return result;
        }

    private Order generateOrder(int i) {
        return null;
    }

    private void saveToDatabase(Order order) {
            // شبیه‌سازی ذخیره در دیتابیس (۱۰۰ میلی‌ثانیه)
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
    }
