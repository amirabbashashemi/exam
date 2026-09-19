package com.example.codeChallenge.deepseek.core.scalability;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UserCache {

    private volatile boolean RUNNING = true;
    private final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private Map<String, User> CACHE_MAP = new ConcurrentHashMap<>();
    private Map<String, Instant> EXPIRY_TIME_MAP = new ConcurrentHashMap<>();
    private final int MAX_SIZE = 10000;
    private final int TTL_SECONDS = 60;

    public UserCache() {
        start();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void put(String key, User user) {
        if (CACHE_MAP.size() >= MAX_SIZE) {
            // اگر کش پر شد، قدیمی‌ترین را پیدا کن
            String oldestKey = null;
            Instant oldestTime = Instant.now();

            for (Map.Entry<String, Instant> entry : EXPIRY_TIME_MAP.entrySet()) {
                if (entry.getValue().isBefore(oldestTime)) {
                    oldestTime = entry.getValue();
                    oldestKey = entry.getKey();
                }
            }

            if (oldestKey != null) {
                CACHE_MAP.remove(oldestKey);
                EXPIRY_TIME_MAP.remove(oldestKey);
            }
        }

        CACHE_MAP.put(key, user);
        EXPIRY_TIME_MAP.put(key, Instant.now().plusSeconds(TTL_SECONDS));
    }

    public User get(String key) {
        Instant expiry = EXPIRY_TIME_MAP.get(key);
        if (expiry == null) {
            return null;
        }

        if (Instant.now().isAfter(expiry)) {
            // منقضی شده
            CACHE_MAP.remove(key);
            EXPIRY_TIME_MAP.remove(key);
            return null;
        }

        return CACHE_MAP.get(key);
    }

    public void cleanup() {
        EXECUTOR_SERVICE.submit(() -> {

            while (RUNNING) {
                // پاک کردن آیتم‌های منقضی شده
                Iterator<Map.Entry<String, Instant>> iterator = EXPIRY_TIME_MAP.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Instant> entry = iterator.next();
                    if (Instant.now().isAfter(entry.getValue())) {
                        CACHE_MAP.remove(entry.getKey());
                        iterator.remove();
                    }
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public void start() {
        cleanup();
    }

    public void shutdown() {
        try {
            RUNNING = false;

            EXECUTOR_SERVICE.shutdown();
            if (!EXECUTOR_SERVICE.awaitTermination(2, TimeUnit.SECONDS)) {
                EXECUTOR_SERVICE.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Shutdown interrupted: " + e.getMessage());
        }
    }
}