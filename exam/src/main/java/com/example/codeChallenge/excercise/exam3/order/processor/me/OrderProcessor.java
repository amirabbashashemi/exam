package com.example.codeChallenge.excercise.exam3.order.processor.me;

/*
یک سرویس OrderProcessor داریم که سفارش‌های مشتریان را پردازش می‌کند و در دیتابیس ذخیره می‌کند.
در حال حاضر روزانه ۱۰۰ سفارش پردازش می‌شود و مشکلی وجود ندارد،
اما مدیرعامل می‌گوید در صورت بروز هرگونه خطا (مثلاً قطع ارتباط با دیتابیس یا خرابی دیسک)، نباید هیچ سفارشی از بین برود
 و سیستم باید بتواند پس از ری‌استارت شدن، کار خود را از همان جایی که قطع شده ادامه دهد و
   اگر سفارشی خراب بود، آن را در یک صف جداگانه برای بررسی دستی قرار دهد  و به پردازش بقیه ادامه دهد.
 */

import com.example.codeChallenge.excercise.exam3.order.processor.Order;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class OrderProcessor {
    private Boolean running;
    private final Path failedPath;
    private final Gson gson = new Gson();
    private final Map<Long, Order> idempotencyMap;
    private final BlockingQueue<Order> orderBlockingQueue;
    private final ExecutorService consumerExecutor = Executors.newSingleThreadExecutor();

    public OrderProcessor(Path failedPath, int queueSize, int dlqQueueSize) {
        this.running = true;
        this.failedPath = failedPath;
        this.idempotencyMap = new ConcurrentHashMap<>();
        this.orderBlockingQueue = new ArrayBlockingQueue<>(queueSize);

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        this.consumerExecutor.submit(this::internalProcess);

        recover();
    }

    private void recover() {
        boolean exists = Files.exists(failedPath);
        if (!exists) {
            return;
        }

        try (BufferedReader bufferedReader = Files.newBufferedReader(failedPath)) {
            String line = bufferedReader.readLine();

            while (line != null) {
                Order order = gson.fromJson(line, Order.class);
                addToQueueForProcessing(order);
                line = bufferedReader.readLine();
            }

            Files.delete(failedPath);
        } catch (Exception e) {
            System.err.printf("Error in method recover. error message : %s", e.getMessage());
        }
    }

    public void process(Order order) {
        // اعتبارسنجی
        if (order.getId() == null) {
            System.err.printf("Invalid order");
            return;
        }

        addToQueueForProcessing(order);
    }

    private void addToQueueForProcessing(Order order) {
        Order ord = idempotencyMap.get(order.getId());

        if (ord == null || !order.isProcessed()) {
            orderBlockingQueue.add(order);
            idempotencyMap.put(order.getId(), order);
        } else {
            System.err.printf("Order %s processed before.", order.getId());
        }
    }

    private void internalProcess() {
        while (running) {
            Order order = null;
            try {
                order = orderBlockingQueue.poll(10, TimeUnit.SECONDS);

                if (order == null) {
                    continue;
                }

                // ذخیره در دیتابیس
                saveToDatabase(order);

                // به‌روزرسانی وضعیت
                updateStatus(order.getId(), "PROCESSED");

                order.setProcessed(true);
            } catch (Exception e) {
                System.err.printf("Error in method internalProcess. error message : %s", e.getMessage());
                saveInFailedOrder(order);
            }
        }
    }

    private void saveToDatabase(Order order) {
        // ذخیره‌سازی در دیتابیس
    }

    private void updateStatus(Long orderId, String status) {
        // به‌روزرسانی وضعیت
    }

    private void stop() {
        try {
            running = false;

            consumerExecutor.shutdown();
            boolean terminated = consumerExecutor.awaitTermination(20, TimeUnit.SECONDS);

            if (!terminated) {
                consumerExecutor.shutdownNow();
            }

            List<Order> orders = new ArrayList<>();
            orderBlockingQueue.drainTo(orders);

            List<Order> notProcessedOrders = orders
                    .stream()
                    .filter(order -> !order.isProcessed())
                    .toList();

            if (!notProcessedOrders.isEmpty()) {
                saveInFailedOrder(notProcessedOrders);
            }
        } catch (Exception e) {
            System.err.printf("Error in method internalProcess. error message : %s", e.getMessage());
        }
    }

    private void saveInFailedOrder(List<Order> orders) {
        StringBuilder jsonOrderStringBuilder = new StringBuilder();
        for (Order order : orders) {
            if (order == null) {
                continue;
            }

            String jsonOrder = gson.toJson(order);
            jsonOrderStringBuilder
                    .append(jsonOrder)
                    .append("\n");
        }

        saveToFile(jsonOrderStringBuilder.toString());
    }

    private void saveInFailedOrder(Order order) {
        try {
            if (order == null) {
                return;
            }

            order.setProcessed(false);
            String jsonOrder = gson.toJson(order);

            saveToFile(jsonOrder+ "\n");
        } catch (Exception e) {
            System.err.printf("Error in method saveInFailedOrder. error message : %s", e.getMessage());
        }
    }

    private void saveToFile(String jsonOrder) {
        try {
            Files.writeString(
                    failedPath,
                    jsonOrder,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.printf("Error in method saveInFailedOrder. error message : %s", e.getMessage());
        }
    }

}