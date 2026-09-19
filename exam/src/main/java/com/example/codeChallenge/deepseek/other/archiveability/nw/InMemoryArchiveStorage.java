package com.example.codeChallenge.deepseek.other.archiveability.nw;

import ir.dotin.archiveability.Order;

import java.util.ArrayList;
import java.util.List;

public class InMemoryArchiveStorage implements ArchiveStorage {
    List<Order> orders = new ArrayList<>();

    @Override
    public Order retrieval(String id) {
        return orders.stream()
                .filter(order -> order.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<String> archiveOrdersOlderThan(List<Order> orders) {
        this.orders.addAll(orders);

        return orders.stream()
                .map(Order::getId)
                .toList();
    }
}
