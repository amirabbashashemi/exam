package com.example.codeChallenge.deepseek.core.elasticity.image;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/*
مشکلات فعلی:
در زمان پیک (۱۰۰,۰۰۰ تصویر)، صف به سرعت پر می‌شود
تعداد تردهای ثابت (۱۰ ترد) برای پردازش کافی نیست
در زمان‌های عادی (۱۰۰۰ تصویر)، ۱۰ ترد بیکار هستند و منابع هدر می‌روند
صف نامحدود (LinkedBlockingQueue) باعث می‌شود حافظه پر شود
سیستم نمی‌تواند خود را با بار تطبیق دهد
 */

public class ImageProcessorOld {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final BlockingQueue<Image> queue = new LinkedBlockingQueue<>();
    private final AtomicInteger processedCount = new AtomicInteger(0);
    private volatile boolean running = true;

    public void start() {
        // ۱۰ ترد ثابت برای پردازش
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                while (running) {
                    try {
                        Image image = queue.poll(1, TimeUnit.SECONDS);
                        if (image != null) {
                            processImage(image);
                            processedCount.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
    }

    public void addImage(Image image) {
        queue.offer(image);
    }

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
        running = false;
        executor.shutdown();
    }
}