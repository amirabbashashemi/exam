package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes;

import codeChallenge.chatgpt.eCommon.payment.PaymentGateway;
import codeChallenge.chatgpt.eCommon.payment.PaymentRequest;
import codeChallenge.chatgpt.eCommon.payment.PaymentResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class Total {
}

//-----availability::begin-----
public class PaymentService {

    private final PaymentGateway gateway;
    private final codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore store;

    public PaymentService(PaymentGateway gateway) throws IOException {
        this.gateway = gateway;
        this.store = new codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore();
    }

    public PaymentResult pay(PaymentRequest request) {
        long correlationId = request.getCorrelationId();

        synchronized (getLock(correlationId)) {

            codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore.PaymentRecord existing = store.get(correlationId);
            if (existing != null && existing.status() == codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore.PaymentStatus.SUCCESS) {
                return existing.result();
            }

            try {
                codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore.PaymentRecord paymentRecord = new codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore.PaymentRecord(correlationId, codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore.PaymentStatus.PENDING, null);
                store.save(paymentRecord);

                PaymentResult result = retry(() -> gateway.pay(request));

                store.save(new codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore.PaymentRecord(correlationId, codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore.PaymentStatus.SUCCESS, result));
                return result;

            } catch (Exception e) {
                try {
                    store.save(new codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore.PaymentRecord(correlationId, codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore.PaymentStatus.FAILED, null));
                } catch (IOException ignored) {}
                throw new RuntimeException("Payment failed for correlationId=" + correlationId, e);
            }
        }
    }

    private PaymentResult retry(Callable<PaymentResult> action) throws Exception {
        int maxRetries = 3;

        for (int i = 1; i <= maxRetries; i++) {
            try {
                return action.call();
            } catch (Exception e) {
                if (i == maxRetries) throw e;
                Thread.sleep(i * 1000L);
            }
        }
        throw new IllegalStateException();
    }

    // simple per-key lock (exam-friendly)
    private Object getLock(long id) {
        return Long.valueOf(id).toString().intern();
    }
}

public class PaymentStore {
    private final Path logFile = Path.of("payments.log");
    private final Map<Long, codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore.PaymentRecord> cache = new ConcurrentHashMap<>();

    public PaymentStore() throws IOException {recover();}

    public synchronized codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore.PaymentRecord get(long correlationId) {return cache.get(correlationId);}

    public synchronized void save(codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore.PaymentRecord record) throws IOException {
        cache.put(record.correlationId(), record);
        Files.writeString(
                logFile,
                record.correlationId() + "," + record.status() + "," + record.result() + "\n",
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    private void recover() throws IOException {
        if (!Files.exists(logFile)) return;

        for (String line : Files.readAllLines(logFile)) {
            String[] parts = line.split(",");
            long id = Long.parseLong(parts[0]);
            codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore.PaymentStatus status = codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore.PaymentStatus.valueOf(parts[1]);

            cache.put(id, new codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore.PaymentRecord(id, status, null));
        }

    }

    enum PaymentStatus {PENDING, SUCCESS, FAILED}

    record PaymentRecord(long correlationId, codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT.PaymentStore.PaymentStatus status, PaymentResult result) {}
}

//-----continuity::end-----

//-----continuity::begin-----
