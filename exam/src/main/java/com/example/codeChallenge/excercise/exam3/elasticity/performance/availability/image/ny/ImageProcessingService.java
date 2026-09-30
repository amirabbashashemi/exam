package com.example.codeChallenge.excercise.exam3.elasticity.performance.availability.image.ny;

/*
یک استارتاپ ایرانی یک سرویس تبدیل عکس به متن (OCR) راه انداخته است. در حال حاضر روزانه ۱۰ کاربر از این سرویس استفاده می‌کنند
 و هر درخواست حدود ۲ ثانیه طول می‌کشد و سرور فعلی به راحتی از پس این بار برمی‌آید، اما مدیرعامل در یک کنفرانس بزرگ از سرویس رونمایی کرده
  و پیش‌بینی می‌شود تا یک ماه آینده روزانه ۵۰۰۰ کاربر از این سرویس استفاده کنند و هر کاربر ممکن است چندین درخواست همزمان ارسال کند
   (مثلاً ۵ عکس را همزمان آپلود کند) و با کد فعلی اگر تعداد کاربران به ۵۰۰۰ برسد سرور کرش می‌کند و هیچ درخواستی پاسخ داده نمی‌شود،
    پس سرویس باید بتواند با افزایش تعداد کاربران خودش را با بار جدید وفق دهد و منابع سرور را بهینه مصرف کند
     و اگر بار بیش از حد شد به جای کرش کردن، درخواست‌های جدید را به شکل مناسبی مدیریت کند.
 */

import java.util.concurrent.*;

public class ImageProcessingService {
    private static boolean RUNNING = false;
    private static final int MULTIPLE = 100;
    private static final int QUEUE_SIZE = 25000;
    private static final int ARRAY_BLOCKING_QUEUE_SIZE = 10000;
    private static BlockingQueue<Image> IMAGE_BLOCKING_QUEUE = new ArrayBlockingQueue<>(QUEUE_SIZE);
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            MULTIPLE * Runtime.getRuntime().availableProcessors(),
            1,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(ARRAY_BLOCKING_QUEUE_SIZE)
    );

    public ImageProcessingService() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

        consume();
    }

    public void process(String imageId, byte[] imageData) {
        offer(imageId, imageData);
    }

    private void offer(String imageId, byte[] imageData) {
        Image image = new Image(imageId, imageData);
        IMAGE_BLOCKING_QUEUE.offer(image);
    }

    private void consume() {
        RUNNING = true;

        while (RUNNING) {
            try {
                Image poll = IMAGE_BLOCKING_QUEUE.poll(5, TimeUnit.SECONDS);

                EXECUTOR_SERVICE.submit(() -> {
                    if (poll != null) {
                        byte[] processed = heavyProcessing(poll.imageData());

                        saveToStorage(poll.imageId(), processed);
                    }
                });
            } catch (Exception e) {
                System.err.printf("An exception occurred while processing. error message : %s", e.getMessage());
            }
        }
    }

    private byte[] heavyProcessing(byte[] data) {
        // شبیه‌سازی پردازش سنگین
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        return data;
    }

    private void saveToStorage(String imageId, byte[] data) {
        // ذخیره‌سازی
    }

    private void stop() {
        try {
            RUNNING = true;

            EXECUTOR_SERVICE.shutdown();
            boolean terminated = EXECUTOR_SERVICE.awaitTermination(20, TimeUnit.SECONDS);
            if (!terminated) {
                EXECUTOR_SERVICE.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.printf("An exception occurred in method stop. . error message : %s", e.getMessage());
        }
    }
}