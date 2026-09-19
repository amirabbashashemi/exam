package com.example.codeChallenge.deepseek.other.archiveability.nw;

import ir.dotin.archiveability.Order;

import java.util.List;

public interface ArchiveStorage {
    Order retrieval(String id);

    List<String> archiveOrdersOlderThan(List<Order> orders);
}
