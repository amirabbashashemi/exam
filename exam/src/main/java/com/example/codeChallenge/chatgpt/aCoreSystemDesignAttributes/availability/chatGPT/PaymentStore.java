package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT;

import codeChallenge.chatgpt.eCommon.payment.PaymentResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class PaymentStore {
    private final Path logFile = Path.of("payments.log");
    private final Map<Long, PaymentRecord> cache = new ConcurrentHashMap<>();

    public PaymentStore() throws IOException {
        recover();
    }

    public synchronized PaymentRecord get(long correlationId) {
        return cache.get(correlationId);
    }

    public synchronized void save(PaymentRecord record) throws IOException {
        cache.put(record.correlationId(), record);
        Files.writeString(
                logFile,
                STR."\{record.correlationId()},\{record.status()},\{record.result()}",
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    private void recover() throws IOException {
        if (!Files.exists(logFile)) return;

        for (String line : Files.readAllLines(logFile)) {
            String[] parts = line.split(",");
            long id = Long.parseLong(parts[0]);
            PaymentStatus status = PaymentStatus.valueOf(parts[1]);

            cache.put(id, new PaymentRecord(id, status, null));
        }

    }

    enum PaymentStatus {PENDING, SUCCESS, FAILED}

    record PaymentRecord(long correlationId, PaymentStatus status, PaymentResult result) {
    }

}