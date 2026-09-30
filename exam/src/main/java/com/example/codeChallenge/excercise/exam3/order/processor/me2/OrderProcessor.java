package com.example.codeChallenge.excercise.exam3.order.processor.me2;

/*
یک سرویس OrderProcessor داریم که سفارش‌های مشتریان را پردازش می‌کند و در دیتابیس ذخیره می‌کند.
در حال حاضر روزانه ۱۰۰ سفارش پردازش می‌شود و مشکلی وجود ندارد،
اما مدیرعامل می‌گوید در صورت بروز هرگونه خطا (مثلاً قطع ارتباط با دیتابیس یا خرابی دیسک)، نباید هیچ سفارشی از بین برود
 و سیستم باید بتواند پس از ری‌استارت شدن، کار خود را از همان جایی که قطع شده ادامه دهد و
   اگر سفارشی خراب بود، آن را در یک صف جداگانه برای بررسی دستی قرار دهد  و به پردازش بقیه ادامه دهد.
 */

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class OrderProcessor {
    private final Path failedPath;
    private boolean running;
    private final Path checkpointPath;
    private final Gson gson = new Gson();
    private final BlockingQueue<Order> orderBlockingQueue;
    private final Map<Long, Order> idempotencyMap = new ConcurrentHashMap<>();
    private final ExecutorService consumerExecutorService = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public OrderProcessor(Path failedPath, Path checkpointPath, int blSize) {
        this.running = true;
        this.failedPath = failedPath;
        this.checkpointPath = checkpointPath;
        this.orderBlockingQueue = new ArrayBlockingQueue<>(blSize);
        this.scheduledExecutorService.scheduleAtFixedRate(this::refreshIdempotencyMap, 0, 5, TimeUnit.MINUTES);

        recover();
        consumerExecutorService.submit(this::consume);
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void recover() {
        addCheckPointsToMap();

        addFailedToQueue();
    }

    private void addCheckPointsToMap() {
        try (BufferedReader bufferedReader = Files.newBufferedReader(checkpointPath)) {
            String line = bufferedReader.readLine();
            while (line != null) {
                Order order = gson.fromJson(line, Order.class);
                idempotencyMap.putIfAbsent(order.getId(), order);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            System.err.printf("Error in method addCheckPointsToMap. error message : %s", e.getMessage());
        }
    }

    private void addFailedToQueue() {
        try (BufferedReader bufferedReader = Files.newBufferedReader(failedPath)) {
            String line = bufferedReader.readLine();
            while (line != null) {
                Order order = gson.fromJson(line, Order.class);

                boolean offer = orderBlockingQueue.offer(order, 5, TimeUnit.SECONDS);
                if (!offer) {
                    System.err.print("orderBlockingQueue is full");
                }

                line = bufferedReader.readLine();
            }
        } catch (Exception e) {
            System.err.printf("orderBlockingQueue is full. error message : %s", e.getMessage());
        }
    }

    private void saveToPath(Order order, Path path) {
        if (order == null) {
            return;
        }
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
                path,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            try {
                String orderString = gson.toJson(order);
                bufferedWriter.write(orderString);
                bufferedWriter.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            System.err.printf("Error in method saveToFailed. error message : %s", e.getMessage());
        }
    }

    private void saveToPath(List<Order> orders, Path path) {
        if (orders == null || orders.isEmpty()) {
            return;
        }
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
                path,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            for (Order order : orders) {
                try {
                    String orderString = gson.toJson(order);
                    bufferedWriter.write(orderString);
                    bufferedWriter.newLine();
                } catch (Exception e) {
                    System.err.printf("Error in method saveToPath with List while save order to file. error message : %s", e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.printf("Error in method saveToPath with List. error message : %s", e.getMessage());
        }
    }

    public void process(Order order) {
        // اعتبارسنجی
        if (order.getId() == null) {
            System.err.print("Invalid order. rejected");
            return;
        }

        order.setCreatedDate(new Date());
        boolean offer = orderBlockingQueue.offer(order);
        if (!offer) {
            System.err.print("orderBlockingQueue is full.");
            saveToPath(order, failedPath);
        }
    }

    private void consume() {
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

                saveToPath(order, checkpointPath);
            } catch (Exception e) {
                System.err.printf("Error in method consume. error message: %s", e.getMessage());
                saveToPath(order, failedPath);
            }
        }
    }

    private void saveToDatabase(Order order) {
        // ذخیره‌سازی در دیتابیس
    }

    private void updateStatus(Long orderId, String status) {
        // به‌روزرسانی وضعیت
    }

    private void shutdown() {
        try {
            running = false;

            consumerExecutorService.shutdown();

            boolean terminated = consumerExecutorService.awaitTermination(20, TimeUnit.SECONDS);

            if (!terminated) {
                consumerExecutorService.shutdownNow();
            }

            scheduledExecutorService.shutdown();

            terminated = scheduledExecutorService.awaitTermination(20, TimeUnit.SECONDS);

            if (!terminated) {
                scheduledExecutorService.shutdownNow();
            }

            saveQueueToFailed();
        } catch (Exception e) {
            System.err.printf("Error in method shutdown. error message: %s", e.getMessage());
        }
    }

    private void saveQueueToFailed() {
        List<Order> orders = new ArrayList<>();
        orderBlockingQueue.drainTo(orders);
        saveToPath(orders, failedPath);
    }

    private void refreshIdempotencyMap() {
        Date now = new Date();
        Date fromTime = Date.from(now.toInstant().minus(5, ChronoUnit.MINUTES));

        List<Long> oldOrders = idempotencyMap.entrySet().stream()
                .filter(entry -> entry.getValue().getCreatedDate().before(fromTime))
                .map(Map.Entry::getKey)
                .toList();

        if (oldOrders.isEmpty()) {
            return;
        }

        oldOrders.forEach(idempotencyMap::remove);
    }

}

