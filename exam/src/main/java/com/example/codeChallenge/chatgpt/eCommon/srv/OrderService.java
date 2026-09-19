package com.example.codeChallenge.chatgpt.eCommon.srv;

import codeChallenge.chatgpt.eCommon.Order;

import java.util.List;

public class OrderService {
    public List<Order> getOrders(String userId) {
        return List.of(new Order());
    }
}
