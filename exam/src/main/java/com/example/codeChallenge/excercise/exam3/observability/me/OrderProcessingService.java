package com.example.codeChallenge.excercise.exam3.observability.me;

/*
یک سرویس OrderProcessingService داریم که سفارش‌های مشتریان را پردازش می‌کند.
 در حال حاضر وقتی خطایی رخ می‌دهد، هیچ لاگ ساختاریافته‌ای ثبت نمی‌شود و توسعه‌دهنده نمی‌تواند بفهمد کدام درخواست مشکل داشته است.
  همچنین هیچ متریکی از تعداد سفارش‌های موفق، ناموفق، یا زمان پردازش وجود ندارد.
   مدیرعامل می‌گوید: «می‌خواهم وقتی سیستم کند شد یا خطا داد، سریع بفهمیم کجای کار می‌لنگد.
    برای هر درخواست یک شناسه یکتا داشته باشیم تا بتوانیم مسیرش را دنبال کنیم.
     همچنین یک اندپوینت سلامت داشته باشیم که وضعیت سرویس را نشان دهد.»
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderProcessingService {
    private static final Logger LOGGER = LogManager.getLogger(OrderProcessingService.class);
    private boolean running = false;
    private final int refreshTimeSecond;
    private final Map<Long, Order> timerMap = new ConcurrentHashMap<>();
    private final AtomicInteger failedCount = new AtomicInteger(0);
    private final AtomicInteger successfulCount = new AtomicInteger(0);
    private final ScheduledExecutorService scheduledExecutorService;

    public OrderProcessingService(int refreshTimeSecond, int refreshTimeIntervalSecond) {
        running = true;
        this.refreshTimeSecond = refreshTimeSecond;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.scheduledExecutorService.scheduleAtFixedRate(this::refreshTimerMap, 0, refreshTimeIntervalSecond, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void process(Order order) {
        try {
            LOGGER.info("start of processing");

            initiate(order);

            // اعتبارسنجی
            if (order.getId() == null) {
                LOGGER.error("Invalid order. traceId: {}", order.getTraceId());
                throw new RuntimeException("Invalid order");
            }

            // ذخیره در دیتابیس
            saveToDatabase(order);

            // ارسال اعلان
            sendNotification(order);

            successfulCount.incrementAndGet();

            LOGGER.info("end of processing order with traceId:{}", order.getTraceId());
        } catch (Exception e) {
            failedCount.incrementAndGet();
            LOGGER.error("error in processing order with id:{} and traceId:{}", order.getId(), order.getTraceId(), e);
            throw new RuntimeException(e);
        } finally {
            order.setEndDate(new Date());
            timerMap.put(order.getId(), order);
        }
    }

    private static void initiate(Order order) {
        try {
            UUID uuid = UUID.randomUUID();
            String randomUUID = uuid.toString();
            order.setTraceId(randomUUID);
            order.setCreatedDate(new Date());
        } catch (Exception e) {
            LOGGER.error("error in method initiate for order with id:{}. errorMessage:{}", order.getId(), e.getMessage());
            throw e;
        }
    }

    private void saveToDatabase(Order order) {
        // ذخیره‌سازی
    }

    private void sendNotification(Order order) {
        // ارسال اعلان
    }

    public int getTotalCount() {
        int i = failedCount.get() + successfulCount.get();
        return i;
    }

    public int getFailedCount() {
        int i = failedCount.get();
        return i;
    }

    public int getSuccessfulCount() {
        int i = successfulCount.get();
        return i;

    }

    public boolean readiness() {
        return !timerMap.isEmpty();
    }

    public boolean liveness() {
        return running;
    }

    private void refreshTimerMap() {
        try {
            Date now = new Date();
            Date fromDate = Date.from(now.toInstant().minus(refreshTimeSecond, ChronoUnit.SECONDS));

            timerMap
                    .entrySet()
                    .removeIf(entry -> entry.getValue().getCreatedDate().before(fromDate));
        } catch (Exception e) {
            LOGGER.info("error in method refreshTimerMap");
            throw new RuntimeException(e);
        }
    }

    private void shutdown() {
        try {
            this.scheduledExecutorService.shutdown();
            boolean terminated = this.scheduledExecutorService.awaitTermination(20, TimeUnit.SECONDS);
            if (!terminated) {
                this.scheduledExecutorService.shutdownNow();
            }
        } catch (Exception e) {
            LOGGER.info("error in method shutdown. error message is {}", e.getMessage());
        }
    }

}