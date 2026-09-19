package com.example.codeChallenge.deepseek.core.continuity;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
/*
مشکلات فعلی:
Single Point of Failure: همه داده‌ها فقط در یک فایل روی یک دیسک ذخیره می‌شوند. اگر دیسک خراب شود، همه داده‌ها از بین می‌روند.
RPO (Recovery Point Objective) بالا: پشتیبان‌گیری فقط هر ۲۴ ساعت انجام می‌شود، بنابراین در صورت فاجعه، حداکثر ۲۴ ساعت داده از دست می‌رود.
RTO (Recovery Time Objective) بالا: بازیابی از یک فایل بزرگ زمان‌بر است و هیچ مکانیزم failover خودکاری وجود ندارد.
عدم وجود Replication: داده‌ها در یک مکان ذخیره می‌شوند و هیچ کپی در مکان دیگر وجود ندارد.
بدون مکانیزم بازیابی خودکار: اگر سرویس crash کند، باید دستی راه‌اندازی شود و داده‌ها از آخرین پشتیبان بازیابی شوند.
 */
public class OrderService {
    private static final String DATA_FILE = "./orders.dat";
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private volatile boolean running = true;

    public OrderService() {
        loadFromDisk();
        // پشتیبان‌گیری خودکار هر ۲۴ ساعت
        scheduler.scheduleAtFixedRate(this::saveToDisk, 24, 24, TimeUnit.HOURS);
    }

    private void loadFromDisk() {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(DATA_FILE)))) {
            Map<String, Order> loaded = (Map<String, Order>) ois.readObject();
            orders.putAll(loaded);
            System.out.println("Loaded " + orders.size() + " orders from disk");
        } catch (Exception e) {
            System.out.println("No existing data found, starting fresh");
        }
    }

    private void saveToDisk() {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(DATA_FILE)))) {
            oos.writeObject(orders);
            System.out.println("Saved " + orders.size() + " orders to disk");
        } catch (IOException e) {
            System.err.println("Failed to save data: " + e.getMessage());
        }
    }

    public void addOrder(Order order) {
        orders.put(order.getId(), order);
        // هر ۱۰۰ سفارش یکبار ذخیره کن
        if (orders.size() % 100 == 0) {
            saveToDisk();
        }
    }

    public Order getOrder(String id) {
        return orders.get(id);
    }

    public void shutdown() {
        running = false;
        saveToDisk();
        scheduler.shutdown();
    }
}