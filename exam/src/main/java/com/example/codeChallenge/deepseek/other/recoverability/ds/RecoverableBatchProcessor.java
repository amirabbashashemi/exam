package com.example.codeChallenge.deepseek.other.recoverability.ds;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class RecoverableBatchProcessor {
    private final Path stateFile;
    private final Path tempFile;
    private final ReentrantLock lock = new ReentrantLock();
    private final TransactionProcessor processor = new TransactionProcessor();

    public RecoverableBatchProcessor(String stateFilePath) {
        this.stateFile = Path.of(stateFilePath);
        this.tempFile = Path.of(stateFilePath + ".tmp");
    }

    public void processAll(List<Transaction> transactions) throws IOException {
        lock.lock();
        try {
            // 1. بازیابی آخرین ID موفق
            String lastId = readLastSuccessfulId();
            int startIndex = lastId == null ? 0 : findIndex(transactions, lastId) + 1;

            // 2. پرچم برای جلوگیری از پردازش مجدد سنگین
            boolean hasFailureSinceLastCheckpoint = false;

            for (int i = startIndex; i < transactions.size(); i++) {
                Transaction tx = transactions.get(i);
                try {
                    processor.process(tx);

                    // 3. فقط اگر خطایی رخ نداده، نقطه‌بازرسی را به‌روز کن
                    if (!hasFailureSinceLastCheckpoint) {
                        saveCheckpoint(tx.id());
                        System.out.println(tx.id() + " done.");
                    }
                    // اگر خطایی رخ داده، نقطه‌بازرسی نمی‌شود تا تراکنش‌های قبل دوباره امتحان شوند

                } catch (TransactionException e) {
                    // 4. شرط اصلی: خطا را بگیر و به تراکنش بعدی برو (Graceful Degradation)
                    hasFailureSinceLastCheckpoint = true;
                    System.err.println("Transaction failed, will retry later: " + tx.id());
                    // ادامه می‌دهیم، نه پرتاب!
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private String readLastSuccessfulId() throws IOException {
        // 5. اول فایل اصلی را بخوان
        if (Files.exists(stateFile)) {
            String id = Files.readString(stateFile).trim();
            if (!id.isEmpty()) return id;
        }

        // 6. اگر فایل اصلی خالی یا خراب است، سراغ فایل موقت برو
        if (Files.exists(tempFile)) {
            String id = Files.readString(tempFile).trim();
            if (!id.isEmpty()) return id;
        }

        // 7. اگر هیچ فایلی سالم نیست، خطای واضح بده (نه بازگشت به صفر!)
        throw new IOException("Both state and temp files are missing or corrupted!");
    }

    private int findIndex(List<Transaction> list, String id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id().equals(id)) return i;
        }
        return -1; // اگر ID پیدا نشد (نباید اتفاق بیفتد)
    }

    private void saveCheckpoint(String id) throws IOException {
        // 8. نوشتن در فایل موقت
        Files.writeString(tempFile, id, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // 9. حرکت اتمی با Fallback
        try {
            Files.move(tempFile, stateFile,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            // اگر ATOMIC_MOVE پشتیبانی نشد، باز هم حرکت بده
            Files.move(tempFile, stateFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}