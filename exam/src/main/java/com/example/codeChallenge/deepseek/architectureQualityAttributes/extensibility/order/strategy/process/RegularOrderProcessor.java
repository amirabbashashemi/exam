package com.example.codeChallenge.deepseek.architectureQualityAttributes.extensibility.order.strategy.process;

import ir.dotin.extensibility.order.Order;

public class RegularOrderProcessor extends BaseOrderProcessor implements OrderProcessor {
    @Override
    public OrderTypeEnum getType() {
        return OrderTypeEnum.REGULAR;
    }

    @Override
    public void process(Order order) {
        // پردازش سفارش معمولی
        System.out.println("Processing regular order: " + order.getId());
        applyRegularDiscount(order);
        saveOrder(order);
    }

    //اینو دست نزدم تا منطق سوال بدون تغییر بماند
    private void applyRegularDiscount(Order order) {
        order.setDiscount(order.getTotal() * 0.05);
    }
}
