package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.refactored;

import codeChallenge.chatgpt.eCommon.payment.PaymentResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentStore {
    private static Map<Long, PaymentRecord> CACHED_PAYMENT = new ConcurrentHashMap<>();


    public PaymentRecord getOrCreatePending(Long correlationId) {
        return CACHED_PAYMENT.computeIfAbsent(correlationId, id -> new PaymentRecord(id, PaymentStatus.PENDING, null));
    }

    public synchronized PaymentRecord get(Long correlationId) {
        return CACHED_PAYMENT.get(correlationId);
    }

    public void markSuccess(Long correlationId, PaymentResult paymentResult) {
        CACHED_PAYMENT.computeIfPresent(correlationId, (k, v) -> new PaymentRecord(correlationId, PaymentStatus.SUCCESSFUL, paymentResult));
    }

    public void markFailed(Long correlationId, PaymentResult paymentResult) {
        CACHED_PAYMENT.computeIfPresent(correlationId, (k, v) -> new PaymentRecord(correlationId, PaymentStatus.FAILED, paymentResult));
    }
}
