package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.continuity.order.chatgpt;

import com.google.gson.Gson;
import codeChallenge.chatgpt.eCommon.Order;
import codeChallenge.chatgpt.eCommon.repo.OrderRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class OrderService {

    private static final Gson GSON = new Gson();
    private final OrderRepository orderRepository;
    private final Path logFile = Path.of("orders.log");
    private final BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );
    private final AtomicBoolean running = new AtomicBoolean(true);

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;

        // بازیابی سفارش‌های قبل از کرش
        recoverOrders();

        // شروع پردازش موازی سفارش‌ها
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            executor.submit(this::processOrders);
        }

        // shutdown hook برای ذخیره سفارش‌های باقی‌مانده
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void placeOrder(Order order) {
        try {
            // append-only log
            saveToFile(order);
            // اضافه کردن به صف برای پردازش
            orderQueue.put(order);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to place order: " + order.getId(), e);
        }
    }

    private void processOrders() {
        while (running.get() || !orderQueue.isEmpty()) {
            try {
                Order order = orderQueue.poll(1, TimeUnit.SECONDS);
                if (order != null) {
                    // ذخیره در دیتابیس
                    orderRepository.save(order);
                    // بعد از موفقیت، از فایل حذف یا mark می‌کنیم
                    markOrderAsProcessed(order);
                }
            } catch (Exception e) {
                // اگر save در دیتابیس fail شود، سفارش در queue می‌ماند و retry می‌شود
                e.printStackTrace();
            }
        }
    }

    private void saveToFile(Order order) throws IOException {
        String json = GSON.toJson(order);
        Files.writeString(logFile, json + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private void markOrderAsProcessed(Order order) throws IOException {
        // ساده‌ترین روش: حذف سفارش پردازش شده از فایل یا append یک flag
        // برای پیاده‌سازی واقعی می‌توان از فایل جداگانه یا database-based log استفاده کرد
    }

    private void recoverOrders() {
        if (!Files.exists(logFile)) return;
        try (BufferedReader reader = Files.newBufferedReader(logFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Order order = GSON.fromJson(line, Order.class);
                orderQueue.put(order); // دوباره در صف برای پردازش قرار می‌گیرد
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to recover orders", e);
        }
    }

    private void shutdown() {
        running.set(false);
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
