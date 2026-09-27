package com.example.codeChallenge.deepseek.architectureQualityAttributes.extendability.order.strategy.process;

import codeChallenge.deepseek.core.extensibility.order.Order;

import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class OrderProcessorService {
    private static final Map<String, OrderProcessor> ORDER_PROCESSOR_MAP = new ConcurrentHashMap<>();

    public OrderProcessorService() {
        ServiceLoader<OrderProcessor> orderProcessors = ServiceLoader.load(OrderProcessor.class);

        orderProcessors.forEach(orderProcessor -> ORDER_PROCESSOR_MAP.put(orderProcessor.getType().name(), orderProcessor));
    }

    public void processOrder(Order order) {
        OrderProcessor orderProcessor = getOrderProcessor(order.getType());

        orderProcessor.process(order);
    }

    private OrderProcessor getOrderProcessor(String orderType) {
        OrderProcessor orderProcessor = ORDER_PROCESSOR_MAP.get(orderType);

        if (Objects.isNull(orderProcessor)) {
            throw new IllegalArgumentException("Unknown order type: " + orderType);
        }

        return orderProcessor;
    }

}
