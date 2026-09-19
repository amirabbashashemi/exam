package com.example.codeChallenge.chatgpt.dOperationalAttributes.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class OrderService {
    private static final MeterRegistry METER_REGISTRY = new SimpleMeterRegistry();
    private static final Timer FAILED_TIMER = Timer.builder("order.timer.failed").register(METER_REGISTRY);
    private static final Timer SUCCESS_TIMER = Timer.builder("order.timer.success").register(METER_REGISTRY);

    private static final Counter FAILED_COUNTER = Counter.builder("counter.failed").register(METER_REGISTRY);
    private static final Counter SUCCESS_COUNTER = Counter.builder("counter.success").register(METER_REGISTRY);


    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    public void placeOrder(String orderId) {
        long start = System.nanoTime();
        LOGGER.info("Placing order {}", orderId);

        try {
            process(orderId);
            recordSuccess(start);
            LOGGER.info("orderId {} done successfully.", orderId);
        } catch (Exception e) {
            LOGGER.error("orderId {} failed.", orderId);
            recordFailed(start);
        }
    }

    private void recordSuccess(long start) {
        SUCCESS_TIMER.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        SUCCESS_COUNTER.increment();
    }

    private void recordFailed(long start) {
        FAILED_TIMER.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        FAILED_COUNTER.increment();
    }

    private void process(String orderId) {
        if ("FAIL".equals(orderId)) {
            throw new RuntimeException("Order failed");
        }
    }
}
