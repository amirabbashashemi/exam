package com.example.codeChallenge.deepseek.core.elasticity.processor;

import java.util.concurrent.*;

public class RequestProcessorOld {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    public void start() {
        // ۱۰ ترد ثابت برای پردازش
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                while (running) {
                    try {
                        Runnable task = workQueue.poll(1, TimeUnit.SECONDS);
                        if (task != null) {
                            task.run();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
    }

    public void submitRequest(Request request) {
        workQueue.offer(() -> processRequest(request));
    }

    private void processRequest(Request request) {
        // پردازش سنگین (۵۰۰ میلی‌ثانیه)
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // ...
    }

    public void shutdown() {
        running = false;
        executor.shutdown();
    }
}