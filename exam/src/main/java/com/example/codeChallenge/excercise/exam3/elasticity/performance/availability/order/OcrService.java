package com.example.codeChallenge.excercise.exam3.elasticity.performance.availability.order;
/*
یک سرویس OcrService داریم که روزانه ۱۰ کاربر از آن استفاده می‌کنند و هر درخواست ۲ ثانیه طول می‌کشد.
 پیش‌بینی می‌شود تعداد کاربران به ۵۰۰۰ برسد. با کد فعلی، اگر تعداد کاربران زیاد شود، سرور کرش می‌کند و هیچ درخواستی پاسخ داده نمی‌شود.
  سرویس باید بتواند با افزایش تعداد کاربران خودش را با بار جدید وفق دهد و منابع را بهینه مدیریت کند.
 */

import java.util.concurrent.*;

public class OcrService {
    private boolean running = false;
    private final int minPoolSize;
    private final int maxPoolSize;
    private final int maxQueueSize;
    private final ThreadPoolExecutor processExecutor;
    private final ScheduledExecutorService schedulerExecutor;
    private final BlockingQueue<Image> imageBlockingQueue;

    public OcrService(int minPoolSize, int maxPoolSize, int maxQueueSize, int queueSize, ExecutorService consumerExecutor) {
        this.minPoolSize = minPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.maxQueueSize = maxQueueSize;
        this.imageBlockingQueue = new ArrayBlockingQueue<>(queueSize);
        this.schedulerExecutor = Executors.newScheduledThreadPool(1);
        this.schedulerExecutor.scheduleAtFixedRate(this::refreshPool, 0, 1, TimeUnit.SECONDS);
        this.processExecutor = new ThreadPoolExecutor(this.minPoolSize,
                this.maxPoolSize,
                1,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(maxQueueSize),
                new ThreadPoolExecutor.CallerRunsPolicy());

        running = true;
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        consumerExecutor.submit(this::consume);
    }

    //auto scaling
    private void refreshPool() {
        int queueSize = processExecutor.getQueue().size();
        int currentPoolSize = processExecutor.getCorePoolSize();

        if (queueSize > (maxQueueSize / 2) && currentPoolSize < maxPoolSize) {
            int corePoolSize = processExecutor.getCorePoolSize();
            processExecutor.setCorePoolSize(corePoolSize + 1);
        } else if (queueSize < (maxQueueSize / 2) && currentPoolSize > minPoolSize) {
            int corePoolSize = processExecutor.getCorePoolSize();
            processExecutor.setCorePoolSize(corePoolSize - 1);
        }
    }

    public boolean process(String imageId, byte[] imageData) {
        boolean offered = imageBlockingQueue.offer(new Image(imageId, imageData));
        if (!offered) {
            System.err.println("Queue is full. Rejecting: " + imageId);
            return false;
        }
        return true;
    }

    private void consume() {
        while (running) {
            try {
                Image image = imageBlockingQueue.poll(10, TimeUnit.SECONDS);
                process(image);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.printf("error in method consume. error message is : %s", e.getMessage());
            }
        }
    }

    public void process(Image image) {
        processExecutor.submit(() -> {
            if (image != null) {
                String text = performOcr(image.imageData());
                saveToDatabase(image.imageId(), text);
            }
        });
    }

    private String performOcr(byte[] data) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        return "extracted text";
    }

    private void saveToDatabase(String imageId, String text) {
        // ذخیره‌سازی
    }

    private void stop() {
        try {
            processExecutor.shutdown();
            boolean processTerminated = processExecutor.awaitTermination(20, TimeUnit.SECONDS);

            if (!processTerminated) {
                processExecutor.shutdownNow();
            }

            schedulerExecutor.shutdown();
            boolean schedulerTerminated = schedulerExecutor.awaitTermination(20, TimeUnit.SECONDS);

            if (!schedulerTerminated) {
                schedulerExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.printf("error in method stop. error message is : %s", e.getMessage());
        }
    }
}