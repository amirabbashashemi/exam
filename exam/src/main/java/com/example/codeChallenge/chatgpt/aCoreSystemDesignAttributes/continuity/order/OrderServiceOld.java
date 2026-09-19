package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.continuity.order;

import codeChallenge.chatgpt.eCommon.Order;
import codeChallenge.chatgpt.eCommon.repo.OrderRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class OrderServiceOld {
    private OrderRepository orderRepository;

    public void processOrder(Order order) throws IOException {
        saveToFile(order);        // ممکن است crash شود
        orderRepository.save(order);  // ممکن است crash شود
    }

    private void saveToFile(Order order) throws IOException {
        Path path = Path.of("orders.log");
        Files.writeString(path, order.toString(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
/*
سؤال:

این سرویس در برابر crash مقاوم نیست.

با استفاده از append-only log و checkpoint یا journal بنویسید که پس از crash یا reboot سرور، پردازش سفارش‌ها از آخرین نقطه امن ادامه پیدا کند.

هدف، ارزیابی Continuity / Disaster Recovery است.
 */
