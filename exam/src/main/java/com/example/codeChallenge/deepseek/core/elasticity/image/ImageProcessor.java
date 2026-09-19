package com.example.codeChallenge.deepseek.core.elasticity.image;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
مشکلات فعلی:
در زمان پیک (۱۰۰,۰۰۰ تصویر)، صف به سرعت پر می‌شود
تعداد تردهای ثابت (۱۰ ترد) برای پردازش کافی نیست
در زمان‌های عادی (۱۰۰۰ تصویر)، ۱۰ ترد بیکار هستند و منابع هدر می‌روند
صف نامحدود (LinkedBlockingQueue) باعث می‌شود حافظه پر شود
سیستم نمی‌تواند خود را با بار تطبیق دهد
 */

public class ImageProcessor {
    private final int batchSize = 100;
    private final ExecutorService executor = new ThreadPoolExecutor(
            5,
            4 * Runtime.getRuntime().availableProcessors(),
            10,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(100));
    private final ScheduledExecutorService listeningExecutor = Executors.newScheduledThreadPool(1);
    private final BlockingQueue<Image> queue = new ArrayBlockingQueue<>(120000);//بیست درصئ بیشتر از پیک
    private final AtomicInteger processedCount = new AtomicInteger(0);
    private volatile boolean running = true;

    public ImageProcessor() {
        start();
    }

    public void addImage(Image image) {
        boolean offer = queue.offer(image);

        if (!offer) {
            System.err.println("The queue is full.");
        }
    }

    public void start() {
        listeningExecutor.scheduleAtFixedRate(this::processBatch, 0, 10, TimeUnit.MILLISECONDS);
    }

    public void processBatch() {
        List<Image> images = new ArrayList<>();
        List<CompletableFuture<Void>> completableFutures = new ArrayList();

        queue.drainTo(images, batchSize);

        images.forEach(image -> {
                    CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                                if (image != null) {
                                    processImage(image);
                                    processedCount.incrementAndGet();
                                }
                            }
                            , executor);

                    completableFutures.add(voidCompletableFuture);
                }
        );

        CompletableFuture
                .allOf(completableFutures.toArray(new CompletableFuture[0]))
                .join();

    }

    //تغییر ندادم که صورت مسیله خراب نشه
    private void processImage(Image image) {
        // پردازش سنگین (۵۰۰ میلی‌ثانیه)
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // تبدیل و ذخیره تصویر
    }

    public void shutdown() {
        try {
            running = false;

            executor.shutdown();
            boolean awaitedTermination = executor.awaitTermination(20, TimeUnit.SECONDS);
            if (!awaitedTermination) {
                executor.shutdownNow();
            }

            listeningExecutor.shutdown();
            awaitedTermination = listeningExecutor.awaitTermination(20, TimeUnit.SECONDS);
            if (!awaitedTermination) {
                listeningExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("Error in shutdown.");
            Thread.currentThread().interrupt();
        }
    }
}