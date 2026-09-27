package com.example.codeChallenge.deepseek.architectureQualityAttributes.extendability.order.strategy.process;

import codeChallenge.deepseek.core.extensibility.order.Order;

public class VipOrderProcessor extends BaseOrderProcessor implements OrderProcessor {
    @Override
    public OrderTypeEnum getType() {
        return OrderTypeEnum.VIP;
    }

    @Override
    public void process(Order order) {
        // پردازش سفارش ویژه
        System.out.println("Processing VIP order: " + order.getId());
        applyVIPDiscount(order);
        sendVIPNotification(order);
        saveOrder(order);
    }

    //اینو دست نزدم تا منطق سوال بدون تغییر بماند
    private void applyVIPDiscount(Order order) {
        order.setDiscount(order.getTotal() * 0.15);
    }

    //اینو دست نزدم تا منطق سوال بدون تغییر بماند
    private void sendVIPNotification(Order order) {
        System.out.println("VIP notification sent for order: " + order.getId());
    }
}
