package com.example.codeChallenge.deepseek.core.elasticity.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class RequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class);

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 4 * Runtime.getRuntime().availableProcessors();
    private static final long KEEP_ALIVE_TIME = 60; // ثانیه
    private static final int QUEUE_CAPACITY = 1000;
    private static final int INNER_QUEUE_CAPACITY = 500;

    private final BlockingQueue<Request> workQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private final ExecutorService consumerExecutor = Executors.newSingleThreadExecutor();
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(INNER_QUEUE_CAPACITY),
            new ThreadPoolExecutor.CallerRunsPolicy() // ← Rejection Policy
    );
    private volatile boolean running = true;

    public RequestProcessor() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        consumerExecutor.submit(this::consume);
    }

    private void consume() {
        while (running) {
            try {
                Request request = workQueue.poll(100, TimeUnit.MILLISECONDS);
                if (request != null) {
                    executor.execute(() -> processRequest(request));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


    public void submitRequest(Request request) {
        boolean offered = workQueue.offer(request);
        if (!offered) {
            // ✅ اگر workQueue پر بود، مستقیم به Executor بده (با مدیریت Rejection)
            try {
                executor.execute(() -> processRequest(request));
            } catch (RejectedExecutionException e) {
                // اگر Executor هم پر بود، به CallerRunsPolicy اعتماد کن (یا لاگ کن)
                logger.warn("Request rejected, using caller thread fallback");
                processRequest(request); // ← Fallback نهایی
            }
        }
    }

    private void processRequest(Request request) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        running = false;
        shutdownExecutor(executor);
        shutdownExecutor(consumerExecutor);
    }

    private void shutdownExecutor(ExecutorService executor) {
        try {
            running = false;

            executor.shutdown();
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.warn("Executor did not terminate");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("InterruptedException in method shutdownExecutor: {}", e.getMessage());
        }
    }
}