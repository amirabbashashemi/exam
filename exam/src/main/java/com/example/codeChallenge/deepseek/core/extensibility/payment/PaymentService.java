package com.example.codeChallenge.deepseek.core.extensibility.payment;

import codeChallenge.deepseek.core.extensibility.payment.strategy.pay.PayerService;
import codeChallenge.deepseek.core.extensibility.payment.strategy.validation.ValidationEnum;
import codeChallenge.deepseek.core.extensibility.payment.strategy.validation.ValidatorService;

/*
مشکلات فعلی:
هر بار که درگاه پرداخت جدید اضافه می‌شود، کلاس اصلی تغییر می‌کند (نقض Open/Closed)
کد processPayment بزرگ است و مدیریت آن سخت است
تنظیمات هر درگاه (clientId, apiKey, merchantId) درون کد سخت‌کد شده است
منطق‌های مختلف (API call, notification, result handling) در هم آمیخته شده‌اند
تست کردن هر درگاه به صورت جداگانه سخت است
 */
public class PaymentService {
    private final PayerService payerService = new PayerService();
    private final ValidatorService validatorService = new ValidatorService();

    public void processPayment(PaymentRequest paymentRequest) {
        validatorService.validate(paymentRequest, ValidationEnum.AMOUNT);
        validatorService.validate(paymentRequest, ValidationEnum.CURRENCY);

        payerService.pay(paymentRequest);
    }

}