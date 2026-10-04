package com.example.codeChallenge.deepseek.core.performance.order.ds;

import java.util.List;

public interface OrderRepository {
    void saveAll(List<Order> orders);
}