package com.example.codeChallenge.deepseek.other.archiveability;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderServiceOld {
    private final List<Order> orders = new ArrayList<>();

    public void addOrder(Order order) {
        orders.add(order);
    }

    public List<Order> getActiveOrders() {
        // برگرداندن سفارشات فعال (غیر بایگانی‌شده)
        return orders.stream()
                .filter(o -> !o.getStatus().equals("ARCHIVED"))
                .collect(Collectors.toList());
    }

    public void archiveOldOrders() {
        // ⚠️ بدون منطق بایگانی! فقط لیست سفارشات را فیلتر می‌کند
        System.out.println("Archive job called, but nothing implemented!");
    }

    // ⚠️ هیچ مکانیزمی برای انتقال به سیستم بایگانی وجود ندارد
}
