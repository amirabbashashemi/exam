package com.example.codeChallenge.deepseek.core.performance.order.ds;




import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class OrderService {
    private final List<Order> orders = new CopyOnWriteArrayList<>(); // فقط برای خواندن نهایی
    private final ExecutorService executor;
    private final OrderRepository repository;
    private final int batchSize;

    public OrderService(OrderRepository repository, int batchSize) {
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.repository = Objects.requireNonNull(repository);
        this.batchSize = batchSize;
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void processOrders() {
        List<Order> newOrders = fetchOrdersFromQueue();

        // تقسیم به دسته‌های batchSize
        List<List<Order>> batches = partition(newOrders, batchSize);

        for (List<Order> batch : batches) {
            // ۱. پردازش موازی داخل دسته (اعتبارسنجی + تخفیف)
            List<CompletableFuture<Order>> futures = batch.stream()
                    .map(order -> CompletableFuture.supplyAsync(() -> {
                        validateOrder(order);
                        double discount = calculateDiscount(order.getTotal());
                        order.setDiscount(discount);
                        return order;
                    }, executor))
                    .toList();

            // ۲. منتظر اتمام دسته (با Timeout)
            try {
                CompletableFuture
                        .allOf(futures.toArray(new CompletableFuture[0]))
                        .get(30, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                System.err.println("Batch timed out, cancelling...");
                futures.forEach(f -> f.cancel(true));
                continue; // یا throw
            } catch (Exception e) {
                System.err.println("Batch failed: " + e.getMessage());
                continue;
            }

            // ۳. جمع‌آوری نتایج موفق
            List<Order> processed = new ArrayList<>(batch.size());
            for (CompletableFuture<Order> f : futures) {
                if (f.isCompletedExceptionally()) continue;
                try {
                    Order o = f.get();
                    if (o != null) processed.add(o);
                } catch (Exception ignored) { }
            }

            // ۴. ذخیره‌ی دسته‌ای در دیتابیس (یک رفت‌وآمد)
            if (!processed.isEmpty()) {
                repository.saveAll(processed);
                orders.addAll(processed);
            }
        }
    }

    private static <T> List<List<T>> partition(List<T> list, int size) {
        return IntStream.range(0, (list.size() + size - 1) / size)
                .mapToObj(i -> list.subList(i * size, Math.min((i + 1) * size, list.size())))
                .toList();
    }

    private static void validateOrder(Order order) {
        for (Product product : order.getProducts()) {
            if (product.getPrice() < 0) {
                throw new IllegalArgumentException("Invalid price");
            }
            if (product.getName() == null || product.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid name");
            }
        }
    }

    private List<Order> fetchOrdersFromQueue() {
        // شبیه‌سازی دریافت ۱۰۰۰۰ سفارش
        return IntStream.range(0, 10_000)
                .mapToObj(i -> generateOrder())
                .toList();
    }

    private Order generateOrder() {
        Order order = new Order();
        order.setTotal(109999);
        order.setProducts(new ArrayList<>());
        return order;
    }

    private double calculateDiscount(int total) {
        if (total > 10000) return total * 0.3;
        if (total > 5000) return total * 0.2;
        if (total > 1000) return total * 0.1;
        return 0;
    }

    private void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}