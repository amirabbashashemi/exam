package com.example.codeChallenge.excercise.exam3.elasticity.performance.availability.image.ny;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProcessService {
    private static final int MULTIPLE = 100;
    private static final int ARRAY_BLOCKING_QUEUE_SIZE = 10000;
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            MULTIPLE * Runtime.getRuntime().availableProcessors(),
            1,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(ARRAY_BLOCKING_QUEUE_SIZE)
    );

    public void process(String imageId, byte[] imageData) {
        EXECUTOR_SERVICE.submit(() -> {
            byte[] processed = heavyProcessing(imageData);
            saveToStorage(imageId, processed);
        });
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

}
