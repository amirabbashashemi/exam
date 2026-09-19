package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.continuity.order;

import com.google.gson.Gson;
import codeChallenge.chatgpt.eCommon.Order;
import codeChallenge.chatgpt.eCommon.repo.OrderRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.*;

public class OrderService {
    private static final Gson GSON = new Gson();
    private OrderRepository orderRepository;
    private static final BlockingQueue<OrderRecord> LONG_ORDER_RECORD_BQ = new LinkedBlockingQueue<>(10000);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public OrderService() {
        recover();
        initiate();
        setShutDownHook();
    }

    private void setShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            EXECUTOR_SERVICE.shutdown();
            try {
                EXECUTOR_SERVICE.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
            }
        }));

    }

    private void initiate() {
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            EXECUTOR_SERVICE.submit(this::createConsumer);
        }
    }

    private void createConsumer() {
        OrderRecord orderRecord = null;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                orderRecord = LONG_ORDER_RECORD_BQ.take();
                if (orderRecord != null) {
                    register(orderRecord);
                }
            }
        } catch (Exception exception) {
            if (orderRecord != null) {
                orderRecord = new OrderRecord(orderRecord.order(), Status.READY_FOR_SAVE_TO_QUEUE);
                LONG_ORDER_RECORD_BQ.add(orderRecord);
            }
            Thread.currentThread().interrupt();
            throw new RuntimeException("An exception occurred in method createConsumer.", exception);
        }
    }

    private void register(OrderRecord orderRecord) {
        try {
            orderRecord = new OrderRecord(orderRecord.order(), Status.READY_FOR_SAVE_TO_FILE);
            saveToFile(orderRecord);

            orderRecord = new OrderRecord(orderRecord.order(), Status.READY_FOR_SAVE_TO_DB);
            orderRepository.save(orderRecord.order());

            orderRecord = new OrderRecord(orderRecord.order(), Status.DONE);
            saveToFile(orderRecord);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveToFile(OrderRecord orderRecord) {
        try {
            Path path = Path.of("orders.log");
            String json = GSON.toJson(orderRecord);
            Files.writeString(path, json + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception exception) {
            throw new RuntimeException("An exception occurred in method saveToFile.", exception);
        }
    }

    private void recover() {
        Path path = Path.of("orders.log");
        Path tempPath = Path.of("orders-temp.log");
        if (!Files.exists(path)) {
            return;
        }

        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                OrderRecord orderRecord = GSON.fromJson(line, OrderRecord.class);
                if (!orderRecord.status().equals(Status.DONE)) {
                    processOrder(orderRecord.order());
                    Files.writeString(tempPath, line + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
            }
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception exception) {
            throw new RuntimeException("An exception occurred in method recover.", exception);
        }
    }

    public void processOrder(Order order) throws IOException {
        OrderRecord orderRecord = new OrderRecord(order, Status.READY_FOR_SAVE_TO_QUEUE);
        LONG_ORDER_RECORD_BQ.add(orderRecord);
    }


}

enum Status {
    READY_FOR_SAVE_TO_QUEUE,
    READY_FOR_SAVE_TO_FILE,
    READY_FOR_SAVE_TO_DB,
    DONE
}

record OrderRecord(Order order, Status status) {
}

/*
سؤال:

این سرویس در برابر crash مقاوم نیست.

با استفاده از append-only log و checkpoint یا journal بنویسید که پس از crash یا reboot سرور، پردازش سفارش‌ها از آخرین نقطه امن ادامه پیدا کند.

هدف، ارزیابی Continuity / Disaster Recovery است.
 */