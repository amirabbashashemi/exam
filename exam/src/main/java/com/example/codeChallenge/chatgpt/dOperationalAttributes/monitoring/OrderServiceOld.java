package com.example.codeChallenge.chatgpt.dOperationalAttributes.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderServiceOld {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceOld.class);

    public void placeOrder(String orderId) {
        long start = System.currentTimeMillis();
        LOGGER.info("Placing order {}", orderId);

        try {
            process(orderId);
            LOGGER.info("Order {} placed successfully in {} ms", orderId, System.currentTimeMillis() - start);
        } catch (Exception e) {
            LOGGER.error("Order {} failed after {} ms", orderId, System.currentTimeMillis() - start);
        }
    }

    private void process(String orderId) {
        if ("FAIL".equals(orderId)) {
            throw new RuntimeException("Order failed");
        }
    }
}
/*
کد زیر از نظر Monitoring مشکلات زیر را دارد:
متریک‌ها قابل جمع‌آوری استاندارد نیستند
داده‌های مانیتورینگ به لاگ وابسته‌اند
امکان Alert دقیق روی latency یا error rate وجود ندارد
وضعیت runtime سرویس قابل مشاهده نیست
 */
