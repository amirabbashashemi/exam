package com.example.codeChallenge.excercise.exam3.order.processor.ds;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.*;

public class OrderProcessor {
    private volatile boolean running = true;
    private final Path failedPath;
    private final Path checkpointPath;
    private final Gson gson;
    private final Map<Long, Boolean> processedIds;
    private final BlockingQueue<Order> orderQueue;
    private final ExecutorService consumerExecutor;
    private final ScheduledExecutorService checkpointCleaner;

    public OrderProcessor(Path failedPath, Path checkpointPath, int queueSize, Gson gson) {
        this.failedPath = failedPath;
        this.checkpointPath = checkpointPath;
        this.gson = gson;
        this.processedIds = new ConcurrentHashMap<>();
        this.orderQueue = new ArrayBlockingQueue<>(queueSize);
        this.consumerExecutor = Executors.newSingleThreadExecutor();
        this.checkpointCleaner = Executors.newScheduledThreadPool(1);

        // ۱. بارگذاری Checkpoint (قبل از هر چیز)
        loadCheckpoint();

        // ۲. بازیابی سفارش‌های خطادار
        recover();

        // ۳. راه‌اندازی Consumer
        this.consumerExecutor.submit(this::internalProcess);

        // ۴. پاک‌سازی دوره‌ای Checkpoint (جلوگیری از Memory Leak)
        this.checkpointCleaner.scheduleAtFixedRate(this::cleanupCheckpoint, 1, 1, TimeUnit.HOURS);

        // ۵. Graceful Shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    // ------------------------------------------------------------
    // Checkpoint: ذخیره‌ی ID سفارش‌های پردازش‌شده
    // ------------------------------------------------------------
    private void loadCheckpoint() {
        if (!Files.exists(checkpointPath)) return;

        try (BufferedReader reader = Files.newBufferedReader(checkpointPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    processedIds.put(Long.parseLong(line.trim()), Boolean.TRUE);
                } catch (NumberFormatException ignored) { }
            }
        } catch (IOException e) {
            System.err.println("Checkpoint load failed: " + e.getMessage());
        }
    }

    private void saveCheckpoint(Long orderId) {
        try {
            Files.writeString(
                    checkpointPath,
                    orderId + "\n",
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            processedIds.put(orderId, Boolean.TRUE);
        } catch (IOException e) {
            System.err.println("Checkpoint save failed: " + e.getMessage());
        }
    }

    private void cleanupCheckpoint() {
        // پاک‌سازی حافظه‌ی in-memory (فایل Checkpoint را دست نمی‌زنیم)
        if (processedIds.size() > 100_000) {
            processedIds.clear();
            loadCheckpoint(); // دوباره از فایل می‌خوانیم
        }
    }

    // ------------------------------------------------------------
    // Recovery: بازیابی سفارش‌های خطادار از DLQ
    // ------------------------------------------------------------
    private void recover() {
        if (!Files.exists(failedPath)) return;

        List<Order> toRecover = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(failedPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Order order = gson.fromJson(line, Order.class);
                    if (order != null && order.getId() != null) {
                        toRecover.add(order);
                    }
                } catch (Exception e) {
                    System.err.println("Invalid DLQ entry: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Recovery read failed: " + e.getMessage());
            return;
        }

        // حالا که همه را خواندیم، فایل را حذف کن (Atomic)
        try {
            Files.deleteIfExists(failedPath);
        } catch (IOException e) {
            System.err.println("Failed to delete DLQ: " + e.getMessage());
        }

        // سفارش‌ها را به صف بفرست (فقط آنهایی که در Checkpoint نیستند)
        for (Order order : toRecover) {
            if (!processedIds.containsKey(order.getId())) {
                addToQueue(order);
            } else {
                System.err.println("Order " + order.getId() + " already processed. Skipping.");
            }
        }
    }

    // ------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------
    public boolean process(Order order) {
        if (order == null || order.getId() == null) {
            System.err.println("Invalid order: null or missing ID");
            return false;
        }

        if (processedIds.containsKey(order.getId())) {
            System.err.println("Order " + order.getId() + " already processed.");
            return false;
        }

        return addToQueue(order);
    }

    private boolean addToQueue(Order order) {
        boolean offered = orderQueue.offer(order);
        if (!offered) {
            System.err.println("Queue is full. Rejecting order: " + order.getId());
            saveInFailedOrder(order); // Fallback: به DLQ بفرست
            return false;
        }
        return true;
    }

    // ------------------------------------------------------------
    // Consumer: پردازش سفارش‌ها
    // ------------------------------------------------------------
    private void internalProcess() {
        while (running) {
            Order order = null;
            try {
                order = orderQueue.poll(10, TimeUnit.SECONDS);
                if (order == null) continue;

                // ۱. ذخیره در دیتابیس
                saveToDatabase(order);

                // ۲. به‌روزرسانی وضعیت
                updateStatus(order.getId(), "PROCESSED");

                // ۳. ثبت در Checkpoint
                saveCheckpoint(order.getId());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (order != null) saveInFailedOrder(order);
                break;
            } catch (Exception e) {
                System.err.println("Error processing order: " + e.getMessage());
                if (order != null) saveInFailedOrder(order);
            }
        }
    }

    private void saveToDatabase(Order order) {
        // ذخیره‌سازی در دیتابیس
    }

    private void updateStatus(Long orderId, String status) {
        // به‌روزرسانی وضعیت
    }

    // ------------------------------------------------------------
    // DLQ: ذخیره‌ی سفارش‌های خطادار
    // ------------------------------------------------------------
    private void saveInFailedOrder(Order order) {
        if (order == null) return;

        try {
            String json = gson.toJson(order);
            Files.writeString(
                    failedPath,
                    json + "\n",
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.println("Failed to save DLQ: " + e.getMessage());
        }
    }

    private void saveInFailedOrder(List<Order> orders) {
        if (orders == null || orders.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        for (Order order : orders) {
            if (order == null) continue;
            sb.append(gson.toJson(order)).append("\n");
        }

        try {
            Files.writeString(
                    failedPath,
                    sb.toString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.println("Failed to save DLQ batch: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // Graceful Shutdown
    // ------------------------------------------------------------
    private void stop() {
        System.out.println("🛑 Shutting down...");
        running = false;

        // ۱. متوقف کردن Consumer
        consumerExecutor.shutdown();
        try {
            if (!consumerExecutor.awaitTermination(20, TimeUnit.SECONDS)) {
                consumerExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            consumerExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // ۲. ذخیره‌ی سفارش‌های باقی‌مانده در صف
        List<Order> remaining = new ArrayList<>();
        orderQueue.drainTo(remaining);

        if (!remaining.isEmpty()) {
            saveInFailedOrder(remaining);
            System.out.println("Saved " + remaining.size() + " remaining orders to DLQ");
        }

        // ۳. متوقف کردن Checkpoint Cleaner
        checkpointCleaner.shutdown();
        try {
            if (!checkpointCleaner.awaitTermination(10, TimeUnit.SECONDS)) {
                checkpointCleaner.shutdownNow();
            }
        } catch (InterruptedException e) {
            checkpointCleaner.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("✅ Shutdown complete");
    }
}
