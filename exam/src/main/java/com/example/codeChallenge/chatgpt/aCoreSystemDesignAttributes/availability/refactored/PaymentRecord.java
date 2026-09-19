package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.availability.refactored;

import codeChallenge.chatgpt.eCommon.payment.PaymentResult;

record PaymentRecord(long correlationId, PaymentStatus paymentStatus, PaymentResult paymentResult) {
}
