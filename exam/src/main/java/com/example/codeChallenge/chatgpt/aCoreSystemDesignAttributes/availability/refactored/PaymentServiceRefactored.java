package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.refactored;

import codeChallenge.chatgpt.eCommon.payment.PaymentGateway;
import codeChallenge.chatgpt.eCommon.payment.PaymentRequest;
import codeChallenge.chatgpt.eCommon.payment.PaymentResult;

public class PaymentServiceRefactored {
    private final PaymentStore paymentStore = new PaymentStore();
    private PaymentGateway gateway;

    public PaymentResult pay(PaymentRequest paymentRequest) throws InterruptedException {
        PaymentRecord paymentRecord = paymentStore.getOrCreatePending(paymentRequest.getCorrelationId());

        if (paymentRecord.paymentStatus().equals(PaymentStatus.SUCCESSFUL)) {
            return paymentRecord.paymentResult();
        }

        return retry(paymentRequest);
    }

    private PaymentResult retry(PaymentRequest paymentRequest) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            try {
                PaymentResult paymentResult = gateway.pay(paymentRequest);

                paymentStore.markSuccess(paymentRequest.getCorrelationId(), paymentResult);

                return paymentResult;
            } catch (RuntimeException e) {
                Thread.sleep((i + 1) * 1000);
            }
        }
        paymentStore.markFailed(paymentRequest.getCorrelationId(), null);
        throw new RuntimeException(String.format("Error in retry for correlationId : %d", paymentRequest.getCorrelationId()));
    }

}

