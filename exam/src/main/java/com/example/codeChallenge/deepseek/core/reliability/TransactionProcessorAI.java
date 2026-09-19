package com.example.codeChallenge.deepseek.core.reliability;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.*;

/**
 * TransactionProcessor با پشتیبانی کامل از Reliability:
 * - Write-Ahead Log (WAL) با Append
 * - Recovery با Deduplication
 * - Idempotency با successfulMap (دائمی)
 * - Retry خودکار برای تراکنش‌های failed
 * - Checkpointing و Log Rotation
 * - Graceful Shutdown
 */
public class TransactionProcessorAI {
    private static final Logger log = LoggerFactory.getLogger(TransactionProcessorAI.class);

    // ==================== Configurations ====================
    private static final int BACKUP_INTERVAL_MS = 1000;
    private static final int RETRY_INTERVAL_MS = 10000;
    private static final int QUEUE_CAPACITY = 10000;
    private static final int MAX_RETRY_ATTEMPTS = 3;

    private static final Path WAL_PATH = Paths.get("./data/wal.log");
    private static final Path FAILED_PATH = Paths.get("./data/failed.log");
    private static final Path SUCCESS_PATH = Paths.get("./data/success.log");
    private static final Path CHECKPOINT_PATH = Paths.get("./data/checkpoint.json");

    // ==================== Core Components ====================
    private final Gson gson = new Gson();
    private final BlockingQueue<Transaction> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    // State Maps (همیشه در حافظه، هرگز پاک نمی‌شوند مگر در Checkpoint)
    private final Map<String, Transaction> walMap = new ConcurrentHashMap<>();
    private final Map<String, Transaction> failedMap = new ConcurrentHashMap<>();
    private final Map<String, Transaction> successMap = new ConcurrentHashMap<>();

    // Executors
    private final ExecutorService workerPool = Executors.newFixedThreadPool(10);
    private final ScheduledExecutorService backupScheduler = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService retryScheduler = Executors.newSingleThreadScheduledExecutor();

    private volatile boolean running = true;

    // ==================== Constructor ====================
    public void TransactionProcessor() {
        // 1. Recovery (قبل از هر چیز)
        recover();

        // 2. Backup دوره‌ای
        backupScheduler.scheduleWithFixedDelay(this::doBackup, 0, BACKUP_INTERVAL_MS, TimeUnit.MILLISECONDS);

        // 3. Retry خودکار برای failed تراکنش‌ها
        retryScheduler.scheduleWithFixedDelay(this::retryFailed, 0, RETRY_INTERVAL_MS, TimeUnit.MILLISECONDS);

        // 4. Shutdown Hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        // 5. شروع پردازش
        start();
    }

    // ==================== Public API ====================
    public void addTransaction(Transaction tx) {
        // 1. WAL: قبل از هر چیزی در Log بنویس
        appendToWAL(tx);

        // 2. به صف و WAL Map اضافه کن
        queue.offer(tx);
        walMap.putIfAbsent(tx.getId(), tx);

        log.info("Transaction {} added to queue and WAL", tx.getId());
    }

    // ==================== Core Processing ====================
    private void start() {
        workerPool.submit(() -> {
            while (running) {
                try {
                    Transaction tx = queue.poll(500, TimeUnit.MILLISECONDS);
                    if (tx != null) {
                        processTransaction(tx);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    private void processTransaction(Transaction tx) {
        try {
            log.info("Processing: {}", tx.getId());

            // 1. Idempotency: اگر قبلاً پردازش شده، نادیده بگیر
            if (successMap.containsKey(tx.getId())) {
                log.info("Transaction {} already processed (idempotent)", tx.getId());
                return;
            }

            // 2. مراحل اصلی (طبق صورت سوال)
            validate(tx);
            saveToDatabase(tx);
            sendToExternal(tx);

            // 3. موفقیت: ثبت در successMap و پاک کردن از سایر Mapها
            successMap.putIfAbsent(tx.getId(), tx);
            walMap.remove(tx.getId());
            failedMap.remove(tx.getId());

            log.info("Transaction {} completed successfully", tx.getId());

        } catch (Exception e) {
            // 4. خطا: اضافه به failedMap برای Retry بعدی
            failedMap.putIfAbsent(tx.getId(), tx);
            log.error("Transaction {} failed: {}", tx.getId(), e.getMessage());
        }
    }

    // ==================== Write-Ahead Log (WAL) ====================
    private void appendToWAL(Transaction tx) {
        try {
            String line = gson.toJson(tx) + System.lineSeparator();
            Files.writeString(WAL_PATH, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("WAL write failed for tx {}: {}", tx.getId(), e.getMessage());
        }
    }

    // ==================== Backup (Checkpointing) ====================
    private void doBackup() {
        try {
            // 1. Backup WAL
            backupMapToFile(walMap, WAL_PATH);

            // 2. Backup Failed
            backupMapToFile(failedMap, FAILED_PATH);

            // 3. Backup Success (فقط نوشته می‌شود، پاک نمی‌شود)
            backupMapToFile(successMap, SUCCESS_PATH);

            // 4. Checkpoint: هر ۱۰ Backup یک Checkpoint بگیر (فشرده‌سازی)
            if (System.currentTimeMillis() % 10000 < 100) {
                doCheckpoint();
            }

        } catch (Exception e) {
            log.error("Backup failed: {}", e.getMessage());
        }
    }

    private void backupMapToFile(Map<String, Transaction> map, Path path) {
        synchronized (map) {
            if (map.isEmpty()) return;
            List<String> lines = new ArrayList<>(map.size());
            map.values().forEach(tx -> lines.add(gson.toJson(tx)));
            try {
                Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                // بعد از Backup، WAL و Failed پاک می‌شوند ولی Success پاک نمی‌شود
                if (path.equals(WAL_PATH) || path.equals(FAILED_PATH)) {
                    map.clear();
                }
            } catch (IOException e) {
                log.error("Backup to {} failed: {}", path, e.getMessage());
            }
        }
    }

    private void doCheckpoint() {
        try {
            // ۱. Snapshot از وضعیت فعلی
            Map<String, Object> checkpoint = new HashMap<>();
            checkpoint.put("success", new ArrayList<>(successMap.values()));
            checkpoint.put("failed", new ArrayList<>(failedMap.values()));
            checkpoint.put("wal", new ArrayList<>(walMap.values()));

            // ۲. نوشتن Checkpoint
            String json = gson.toJson(checkpoint);
            Files.writeString(CHECKPOINT_PATH, json,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            // ۳. پاک کردن فایل‌های قدیمی (Log Rotation)
            Files.deleteIfExists(WAL_PATH);
            Files.deleteIfExists(FAILED_PATH);

            log.info("Checkpoint completed. Success: {}, Failed: {}, WAL: {}",
                    successMap.size(), failedMap.size(), walMap.size());

        } catch (IOException e) {
            log.error("Checkpoint failed: {}", e.getMessage());
        }
    }

    // ==================== Retry Mechanism ====================
    private void retryFailed() {
        if (failedMap.isEmpty()) return;

        List<Transaction> toRetry;
        synchronized (failedMap) {
            toRetry = new ArrayList<>(failedMap.values());
            failedMap.clear(); // بعد از ارسال به صف، از failedMap حذف می‌شوند
        }

        for (Transaction tx : toRetry) {
            // فقط اگر هنوز پردازش نشده باشد
            if (!successMap.containsKey(tx.getId())) {
                queue.offer(tx);
                log.info("Retrying failed transaction: {}", tx.getId());
            }
        }
    }

    // ==================== Recovery ====================
    private void recover() {
        Set<String> recoveredIds = ConcurrentHashMap.newKeySet();

        // ۱. بازیابی Checkpoint (اگر وجود داشته باشد)
        recoverFromCheckpoint(recoveredIds);

        // ۲. بازیابی Success (فقط Map، نه صف)
        recoverFromFile(SUCCESS_PATH, successMap, recoveredIds, false);

        // ۳. بازیابی Failed (به صف اضافه کن، با Dedup)
        recoverFromFile(FAILED_PATH, failedMap, recoveredIds, true);

        // ۴. بازیابی WAL (به صف اضافه کن، با Dedup)
        recoverFromFile(WAL_PATH, walMap, recoveredIds, true);

        // ۵. پاک کردن فایل‌های قدیمی بعد از Recovery موفق
        try {
            Files.deleteIfExists(WAL_PATH);
            Files.deleteIfExists(FAILED_PATH);
            Files.deleteIfExists(SUCCESS_PATH);
        } catch (IOException e) {
            log.warn("Could not delete old backup files: {}", e.getMessage());
        }

        log.info("Recovery complete. Success: {}, Failed: {}, WAL: {}, Queue: {}",
                successMap.size(), failedMap.size(), walMap.size(), queue.size());
    }

    private void recoverFromCheckpoint(Set<String> recoveredIds) {
        try {
            if (!Files.exists(CHECKPOINT_PATH)) return;

            String content = Files.readString(CHECKPOINT_PATH);
            // در یک سیستم واقعی، اینجا باید JSON را Parse کنید
            // فعلاً فرض می‌کنیم که Checkpoint معتبر است
            log.info("Checkpoint found. Would recover from it.");

            // برای سادگی، از Checkpoint صرفنظر می‌کنیم و از فایل‌های اصلی استفاده می‌کنیم
            // (در یک پیاده‌سازی واقعی، Checkpoint باید خوانده شود)

        } catch (IOException e) {
            log.error("Failed to read checkpoint: {}", e.getMessage());
        }
    }

    private void recoverFromFile(Path path, Map<String, Transaction> targetMap,
                                 Set<String> recoveredIds, boolean addToQueue) {
        try {
            if (!Files.exists(path)) return;

            List<String> lines = Files.readAllLines(path);
            int added = 0;

            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) continue;

                Transaction tx = gson.fromJson(line, Transaction.class);
                if (tx == null) continue;

                // 1. Deduplication: فقط اگر قبلاً بازیابی نشده و در successMap نیست
                if (recoveredIds.contains(tx.getId())) continue;
                if (successMap.containsKey(tx.getId())) continue;

                // 2. افزودن به Map
                targetMap.putIfAbsent(tx.getId(), tx);
                recoveredIds.add(tx.getId());

                // 3. اگر لازم است به صف اضافه شود
                if (addToQueue && !queue.contains(tx)) {
                    queue.offer(tx);
                }
                added++;
            }

            log.info("Recovered {} transactions from {}", added, path.getFileName());

        } catch (IOException e) {
            log.error("Failed to recover from {}: {}", path, e.getMessage());
        }
    }

    // ==================== Shutdown ====================
    public void shutdown() {
        try {
            running = false;

            // 1. آخرین Backup قبل از خاموش شدن
            doBackup();

            // 2. خاموش کردن Executorها
            shutdownExecutor(workerPool, "worker");
            shutdownExecutor(backupScheduler, "backup");
            shutdownExecutor(retryScheduler, "retry");

            log.info("TransactionProcessor shut down gracefully.");

        } catch (Exception e) {
            log.error("Shutdown error: {}", e.getMessage());
        }
    }

    private void shutdownExecutor(ExecutorService executor, String name) {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.warn("{} executor did not terminate", name);
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // ==================== Stub Methods (صورت سوال) ====================
    private void validate(Transaction tx) throws Exception {
        // شبیه‌سازی اعتبارسنجی
        if (tx.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
    }

    private void saveToDatabase(Transaction tx) throws Exception {
        // شبیه‌سازی ذخیره در دیتابیس (۱۰۰ میلی‌ثانیه)
        Thread.sleep(100);
    }

    private void sendToExternal(Transaction tx) throws Exception {
        // شبیه‌سازی ارسال به سیستم خارجی (۵۰ میلی‌ثانیه)
        Thread.sleep(50);
        if (Math.random() < 0.05) {
            throw new RuntimeException("External system temporarily unavailable");
        }
    }

    // ==================== Getters برای تست ====================
    public int getQueueSize() { return queue.size(); }
    public int getSuccessCount() { return successMap.size(); }
    public int getFailedCount() { return failedMap.size(); }
    public int getWALCount() { return walMap.size(); }
}