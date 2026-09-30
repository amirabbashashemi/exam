package com.example.codeChallenge.excercise.exam3.elasticity.performance.availability.image.ds;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ImageProcessingService {
    private final BlockingQueue<Image> imageQueue;
    private final ThreadPoolExecutor executor;
    private final ScheduledExecutorService autoScaler;
    private final ScheduledExecutorService batchFlusher;
    private final List<Image> batchBuffer;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final int maxQueueSize;
    private final int minThreads;
    private final int maxThreads;

    public ImageProcessingService(int maxQueueSize, int minThreads, int maxThreads) {
        this.maxQueueSize = maxQueueSize;
        this.minThreads = minThreads;
        this.maxThreads = maxThreads;
        this.imageQueue = new ArrayBlockingQueue<>(maxQueueSize);
        this.batchBuffer = new ArrayList<>();
        this.executor = createExecutor();
        this.autoScaler = Executors.newScheduledThreadPool(1);
        this.batchFlusher = Executors.newScheduledThreadPool(1);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        startAutoScaling();
        startBatchFlushing();
        startConsumer();
    }

    // ۱. ایجاد ThreadPoolExecutor با Rejection Policy
    private ThreadPoolExecutor createExecutor() {
        return new ThreadPoolExecutor(
                minThreads,
                maxThreads,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(maxQueueSize),
                new ThreadPoolExecutor.CallerRunsPolicy()  // Rejection Policy
        );
    }

    // ۲. شروع Auto-scaling
    private void startAutoScaling() {
        autoScaler.scheduleAtFixedRate(() -> {
            int queueSize = executor.getQueue().size();
            int currentPoolSize = executor.getPoolSize();

            if (queueSize > maxQueueSize / 2 && currentPoolSize < maxThreads) {
                // افزایش تردها
                int newSize = Math.min(maxThreads, currentPoolSize + 2);
                executor.setCorePoolSize(newSize);
                System.out.println("⬆️ Scale UP: " + currentPoolSize + " → " + newSize);
            } else if (queueSize < maxQueueSize / 4 && currentPoolSize > minThreads) {
                // کاهش تردها
                int newSize = Math.max(minThreads, currentPoolSize - 1);
                executor.setCorePoolSize(newSize);
                System.out.println("⬇️ Scale DOWN: " + currentPoolSize + " → " + newSize);
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    // ۳. شروع Batch Flushing (ذخیره‌سازی دسته‌ای)
    private void startBatchFlushing() {
        batchFlusher.scheduleAtFixedRate(() -> {
            List<Image> toFlush;
            synchronized (batchBuffer) {
                if (batchBuffer.isEmpty()) return;
                toFlush = new ArrayList<>(batchBuffer);
                batchBuffer.clear();
            }
            saveBatchToStorage(toFlush);
        }, 2, 2, TimeUnit.SECONDS);
    }

    // ۴. شروع Consumer در یک Virtual Thread
    private void startConsumer() {
        running.set(true);
        Thread.ofVirtual().name("image-consumer").start(() -> {
            while (running.get()) {
                try {
                    Image image = imageQueue.poll(1, TimeUnit.SECONDS);
                    if (image != null) {
                        processImage(image);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    // ۵. متد اصلی پردازش (با Fallback)
    public boolean process(String imageId, byte[] imageData) {
        Image image = new Image(imageId, imageData);

        // چک کردن ظرفیت صف
        if (imageQueue.remainingCapacity() == 0) {
            System.err.println("⚠️ Queue is full. Rejecting image: " + imageId);
            return false; // Fallback
        }

        boolean offered = imageQueue.offer(image);
        if (!offered) {
            System.err.println("⚠️ Failed to enqueue image: " + imageId);
            return false;
        }
        return true;
    }

    // ۶. پردازش تصویر با Timeout
    private void processImage(Image image) {
        Future<?> future = executor.submit(() -> {
            try {
                byte[] processed = heavyProcessing(image.imageData());
                addToBatch(image.imageId(), processed);
            } catch (Exception e) {
                System.err.println("Error processing image: " + image.imageId());
            }
        });

        try {
            // Timeout 5 ثانیه‌ای
            future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            System.err.println("⏱️ Timeout! Cancelled processing: " + image.imageId());
        } catch (Exception e) {
            System.err.println("Error waiting for image: " + image.imageId());
        }
    }

    // ۷. افزودن به Batch
    private void addToBatch(String imageId, byte[] data) {
        synchronized (batchBuffer) {
            batchBuffer.add(new Image(imageId, data));
            if (batchBuffer.size() >= 10) {
                List<Image> toSave = new ArrayList<>(batchBuffer);
                batchBuffer.clear();
                saveBatchToStorage(toSave);
            }
        }
    }

    // ۸. ذخیره‌سازی دسته‌ای
    private void saveBatchToStorage(List<Image> images) {
        System.out.println("💾 Saving batch of " + images.size() + " images");
        // ذخیره‌سازی واقعی اینجا
    }

    // ۹. پردازش سنگین (شبیه‌سازی)
    private byte[] heavyProcessing(byte[] data) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return data;
    }

    // ۱۰. Graceful Shutdown
    private void shutdown() {
        System.out.println("🛑 Shutting down...");
        running.set(false);

        // متوقف کردن Auto-scaler و Batch Flusher
        autoScaler.shutdown();
        batchFlusher.shutdown();

        // ذخیره‌ی باقیمانده‌ی Batch
        synchronized (batchBuffer) {
            if (!batchBuffer.isEmpty()) {
                saveBatchToStorage(new ArrayList<>(batchBuffer));
                batchBuffer.clear();
            }
        }

        // متوقف کردن Executor
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("✅ Shutdown completed");
    }

    // ۱۱. Health Check
    public String getHealthStatus() {
        return String.format(
                "Health: {running=%s, queueSize=%d, activeThreads=%d, poolSize=%d, completedTasks=%d}",
                running.get(),
                imageQueue.size(),
                executor.getActiveCount(),
                executor.getPoolSize(),
                executor.getCompletedTaskCount()
        );
    }

    // ۱۲. کلاس داخلی Image
    public record Image(String imageId, byte[] imageData) {}
}