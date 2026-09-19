package com.example.codeChallenge.deepseek.core.performance.order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class OrderService {
    private final List<Order> orders = new CopyOnWriteArrayList<>();
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public OrderService() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));
    }

    public void processOrders() {
        // دریافت سفارشات از یک منبع داده
        List<Order> newOrders = fetchOrdersFromQueue();
        List<CompletableFuture<Order>> completableFutures = new ArrayList<>();

        for (Order order : newOrders) {
            CompletableFuture<Order> voidCompletableFuture = CompletableFuture
                    .supplyAsync(() -> {
                        //  اعتبارسنجی
                        validateOrder(order);

                        // محاسبه تخفیف
                        double discount = calculateDiscount(order.getTotal());
                        order.setDiscount(discount);

                        return order;
                    })
                    .thenApplyAsync(ord -> {
                        saveToDatabase(ord);
                        return ord;
                    }, executorService);

            completableFutures.add(voidCompletableFuture);
        }

        CompletableFuture
                .allOf(completableFutures.toArray(new CompletableFuture[0]))
                .join();

        completableFutures.forEach(orderCompletableFuture -> {
                    Order order = null;
                    try {
                        order = orderCompletableFuture.get();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Interrupted: " + e.getMessage());
                    } catch (ExecutionException e) {
                        System.err.println("Execution error: " + e.getCause().getMessage());
                    }
                    orders.add(order);
                }
        );
    }

    private static void validateOrder(Order order) {
        // اعتبارسنجی محصولات
        for (Product product : order.getProducts()) {
            if (product.getPrice() < 0) {
                throw new IllegalArgumentException("Invalid price");
            } else if (product.getName() == null || product.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid name");
            }
            // اعتبارسنجی بیشتر...
        }
    }

    private List<Order> fetchOrdersFromQueue() {
        // شبیه‌سازی دریافت ۱۰۰۰۰ سفارش
        return Stream.generate(() -> generateOrder())
                .limit(10000)
                .toList();
    }

    private void saveToDatabase(Order order) {
        // شبیه‌سازی ذخیره در دیتابیس (۱۰۰ میلی‌ثانیه)
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted: " + e.getMessage());
        }
    }

    private Order generateOrder() {
        List<Product> list = new ArrayList<>();
        Order order = new Order();
        order.setTotal(109999);
        order.setProducts(list);

        return order;
    }

    private double calculateDiscount(int total) {
        if (total > 10000) {
            return total * 0.3;
        } else if (total > 5000) {
            return total * 0.2;
        } else if (total > 1000) {
            return total * 0.1;
        } else {
            return 0;
        }

    }

    private void shutdown() {
        try {
            executorService.shutdown();
            boolean isShutdown = executorService.awaitTermination(30, TimeUnit.SECONDS);
            if (!isShutdown) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted: " + e.getMessage());
        }

    }
}