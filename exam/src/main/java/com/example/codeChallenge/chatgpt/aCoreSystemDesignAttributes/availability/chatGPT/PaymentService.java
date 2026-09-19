package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.chatGPT;

import codeChallenge.chatgpt.eCommon.payment.PaymentGateway;
import codeChallenge.chatgpt.eCommon.payment.PaymentRequest;
import codeChallenge.chatgpt.eCommon.payment.PaymentResult;

import java.io.IOException;
import java.util.concurrent.Callable;


public class PaymentService {

    private final PaymentGateway gateway;
    private final PaymentStore store;

    public PaymentService(PaymentGateway gateway) throws IOException {
        this.gateway = gateway;
        this.store = new PaymentStore();
    }

    public PaymentResult pay(PaymentRequest request) {
        long correlationId = request.getCorrelationId();

        synchronized (getLock(correlationId)) {

            PaymentStore.PaymentRecord existing = store.get(correlationId);
            if (existing != null && existing.status() == PaymentStore.PaymentStatus.SUCCESS) {
                return existing.result();
            }

            try {
                PaymentStore.PaymentRecord paymentRecord = new PaymentStore.PaymentRecord(correlationId, PaymentStore.PaymentStatus.PENDING, null);
                store.save(paymentRecord);

                PaymentResult result = retry(() -> gateway.pay(request));

                store.save(new PaymentStore.PaymentRecord(correlationId, PaymentStore.PaymentStatus.SUCCESS, result));
                return result;

            } catch (Exception e) {
                try {
                    store.save(new PaymentStore.PaymentRecord(
                            correlationId,
                            PaymentStore.PaymentStatus.FAILED,
                            null));
                } catch (IOException ignored) {
                }
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
