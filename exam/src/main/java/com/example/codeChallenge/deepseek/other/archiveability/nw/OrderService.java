package com.example.codeChallenge.deepseek.other.archiveability.nw;

import ir.dotin.archiveability.Order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class OrderService {
    private final ArchiveStorage archiveStorage;
    private final static int ARCHIVE_INTERVAL = 1;//day
    private final static int ARCHIVE_EXECUTOR_INTERVAL = 20;//second
    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    private List<Order> orders = new ArrayList<>();

    public OrderService(ArchiveStorage archiveStorage) {
        scheduledExecutorService.scheduleAtFixedRate(this::archiveOldOrders, ARCHIVE_EXECUTOR_INTERVAL, 0, TimeUnit.SECONDS);
        this.archiveStorage = archiveStorage;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public List<Order> getActiveOrders() {
        // برگرداندن سفارشات فعال (غیر بایگانی‌شده)
        return orders.stream()
                .filter(o -> !o.getStatus().equals("ARCHIVED"))
                .collect(Collectors.toList());
    }

    public void archiveOldOrders() {
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            LocalDate cutoffDate = LocalDate.now().minusDays(ARCHIVE_INTERVAL);

            List<Order> archiveList = orders.stream()
                    .filter(order -> order.getOrderDate().isBefore(cutoffDate))
                    .toList();

            List<String> archivedIds = archiveStorage.archiveOrdersOlderThan(archiveList);

            if (archivedIds != null && !archivedIds.isEmpty()) {
                orders = orders.stream()
                        .filter(order -> !"ARCHIVED".equals(order.getStatus()) && !archivedIds.contains(order.getId()))
                        .toList();
            }
        } finally {
            lock.unlock();
        }
    }

    public Order search(String id) {
        return orders.stream()
                .filter(o -> !o.getStatus().equals("ARCHIVED") && o.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Order retrieval(String id) {
        return archiveStorage.retrieval(id);
    }

}
