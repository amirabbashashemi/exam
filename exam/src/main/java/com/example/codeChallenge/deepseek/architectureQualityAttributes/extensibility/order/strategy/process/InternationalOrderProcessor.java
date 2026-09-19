package com.example.codeChallenge.deepseek.architectureQualityAttributes.extensibility.order.strategy.process;

import ir.dotin.extensibility.order.Order;

public class InternationalOrderProcessor extends BaseOrderProcessor implements OrderProcessor {

    @Override
    public OrderTypeEnum getType() {
        return OrderTypeEnum.INTERNATIONAL;
    }

    @Override
    public void process(Order order) {
        // پردازش سفارش بین‌المللی
        System.out.println("Processing international order: " + order.getId());
        applyInternationalDiscount(order);
        validateInternationalShipping(order);
        saveOrder(order);
        sendInternationalNotification(order);
    }

    //اینو دست نزدم تا منطق سوال بدون تغییر بماند
    private void applyInternationalDiscount(Order order) {
        order.setDiscount(order.getTotal() * 0.10);
    }

    //اینو دست نزدم تا منطق سوال بدون تغییر بماند
    private void validateInternationalShipping(Order order) {
        System.out.println("International shipping validated for order: " + order.getId());
    }

    //اینو دست نزدم تا منطق سوال بدون تغییر بماند
    private void sendInternationalNotification(Order order) {
        System.out.println("International notification sent for order: " + order.getId());
    }
}
