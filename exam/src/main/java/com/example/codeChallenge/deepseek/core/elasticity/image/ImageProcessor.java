package com.example.codeChallenge.deepseek.core.elasticity.image;






import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ImageProcessor {
    private final ThreadPoolExecutor executor;
    private final ScheduledExecutorService autoScaler;
    private final int minCorePoolSize;
    private final int maxCorePoolSize;
    private final int queueCapacity;
    private final int scaleUpThreshold;
    private final int scaleDownThreshold;

    private final AtomicLong processedCount = new AtomicLong(0);
    private final AtomicLong rejectedCount = new AtomicLong(0);
    private final AtomicLong failedCount = new AtomicLong(0);

    public ImageProcessor(int minCorePoolSize,
                          int maxCorePoolSize,
                          int queueCapacity,
                          int scaleUpThreshold,
                          int scaleDownThreshold) {
        if (minCorePoolSize <= 0 || maxCorePoolSize < minCorePoolSize || queueCapacity <= 0) {
            throw new IllegalArgumentException("Invalid configuration");
        }
        this.minCorePoolSize = minCorePoolSize;
        this.maxCorePoolSize = maxCorePoolSize;
        this.queueCapacity = queueCapacity;
        this.scaleUpThreshold = scaleUpThreshold;
        this.scaleDownThreshold = scaleDownThreshold;

        // ۱. ThreadPoolExecutor با Bounded Queue و Rejection Policy
        this.executor = new ThreadPoolExecutor(
                minCorePoolSize,
                maxCorePoolSize,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // ۲. Auto-Scaler
        this.autoScaler = Executors.newSingleThreadScheduledExecutor();
        this.autoScaler.scheduleAtFixedRate(this::scale, 5, 5, TimeUnit.SECONDS);

        // ۳. Graceful Shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    // ------------------------------------------------------------
    // Auto-Scaling
    // ------------------------------------------------------------
    private void scale() {
        int queueSize = executor.getQueue().size();
        int currentCore = executor.getCorePoolSize();

        if (queueSize > scaleUpThreshold && currentCore < maxCorePoolSize) {
            int newSize = Math.min(maxCorePoolSize, currentCore + 1);
            executor.setCorePoolSize(newSize);
            System.out.printf("⬆️ Scale UP: %d → %d (queue: %d)%n",
                    currentCore, newSize, queueSize);
        } else if (queueSize < scaleDownThreshold && currentCore > minCorePoolSize) {
            int newSize = Math.max(minCorePoolSize, currentCore - 1);
            executor.setCorePoolSize(newSize);
            System.out.printf("⬇️ Scale DOWN: %d → %d (queue: %d)%n",
                    currentCore, newSize, queueSize);
        }
    }

    // ------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------
    public boolean addImage(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        try {
            // هر تصویر یک تسک جداگانه در Executor
            executor.submit(() -> {
                try {
                    processImage(image);
                    processedCount.incrementAndGet();
                } catch (Exception e) {
                    failedCount.incrementAndGet();
                    System.err.println("Error processing image: " + e.getMessage());
                }
            });
            return true;
        } catch (RejectedExecutionException e) {
            // Fallback: CallerRunsPolicy نباید اینجا Exception بدهد،
            // ولی اگر Executor shutdown باشد، این اتفاق می‌افتد
            rejectedCount.incrementAndGet();
            System.err.println("⚠️ Image rejected: executor is shutting down");
            return false;
        }
    }

    // ------------------------------------------------------------
    // Processing
    // ------------------------------------------------------------
    private void processImage(Image image) {
        try {
            Thread.sleep(500); // شبیه‌سازی پردازش سنگین
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // تبدیل و ذخیره تصویر
    }

    // ------------------------------------------------------------
    // Graceful Shutdown
    // ------------------------------------------------------------
    public void shutdown() {
        System.out.println("🛑 Shutting down ImageProcessor...");
        autoScaler.shutdown();

        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.printf("✅ Shutdown complete. Processed: %d, Rejected: %d, Failed: %d%n",
                processedCount.get(), rejectedCount.get(), failedCount.get());
    }

    // ------------------------------------------------------------
    // Health Check
    // ------------------------------------------------------------
    public String getHealthStatus() {
        return String.format(
                "ImageProcessor{corePool=%d, poolSize=%d, activeThreads=%d, " +
                        "queueSize=%d/%d, completed=%d, rejected=%d, failed=%d}",
                executor.getCorePoolSize(),
                executor.getPoolSize(),
                executor.getActiveCount(),
                executor.getQueue().size(),
                queueCapacity,
                processedCount.get(),
                rejectedCount.get(),
                failedCount.get()
        );
    }
}