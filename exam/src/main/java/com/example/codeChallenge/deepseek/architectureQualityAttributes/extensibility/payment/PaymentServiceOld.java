package com.example.codeChallenge.deepseek.architectureQualityAttributes.extensibility.payment;

import java.util.HashMap;
import java.util.Map;

/*
مشکلات فعلی:
هر بار که درگاه پرداخت جدید اضافه می‌شود، کلاس اصلی تغییر می‌کند (نقض Open/Closed)
کد processPayment بزرگ است و مدیریت آن سخت است
تنظیمات هر درگاه (clientId, apiKey, merchantId) درون کد سخت‌کد شده است
منطق‌های مختلف (API call, notification, result handling) در هم آمیخته شده‌اند
تست کردن هر درگاه به صورت جداگانه سخت است
 */
public class PaymentServiceOld {
    private final Map<String, Payment> payments = new HashMap<>();

    public void processPayment(PaymentRequest request) {
        // اعتبارسنجی عمومی
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
        if (request.getCurrency() == null) {
            throw new IllegalArgumentException("Currency required");
        }

        // پردازش بر اساس درگاه پرداخت
        if ("PAYPAL".equals(request.getGateway())) {
            // PayPal
            System.out.println("Processing PayPal payment: " + request.getId());

            // تنظیمات PayPal
            String clientId = "paypal_client_123";
            String secret = "paypal_secret_456";

            // فراخوانی API PayPal
            boolean success = callPayPalAPI(request, clientId, secret);

            if (success) {
                Payment payment = new Payment(request.getId(), "PAYPAL", request.getAmount(), "SUCCESS");
                payments.put(request.getId(), payment);
                sendPayPalNotification(request);
            } else {
                Payment payment = new Payment(request.getId(), "PAYPAL", request.getAmount(), "FAILED");
                payments.put(request.getId(), payment);
            }

        } else if ("STRIPE".equals(request.getGateway())) {
            // Stripe
            System.out.println("Processing Stripe payment: " + request.getId());

            String apiKey = "stripe_api_key_789";
            String webhookSecret = "stripe_webhook_secret";

            boolean success = callStripeAPI(request, apiKey, webhookSecret);

            if (success) {
                Payment payment = new Payment(request.getId(), "STRIPE", request.getAmount(), "SUCCESS");
                payments.put(request.getId(), payment);
                sendStripeNotification(request);
            } else {
                Payment payment = new Payment(request.getId(), "STRIPE", request.getAmount(), "FAILED");
                payments.put(request.getId(), payment);
            }

        } else if ("ZARINPAL".equals(request.getGateway())) {
            // Zarinpal
            System.out.println("Processing Zarinpal payment: " + request.getId());

            String merchantId = "zarinpal_merchant_123";
            String callbackUrl = "https://example.com/callback";

            boolean success = callZarinpalAPI(request, merchantId, callbackUrl);

            if (success) {
                Payment payment = new Payment(request.getId(), "ZARINPAL", request.getAmount(), "SUCCESS");
                payments.put(request.getId(), payment);
                sendZarinpalNotification(request);
            } else {
                Payment payment = new Payment(request.getId(), "ZARINPAL", request.getAmount(), "FAILED");
                payments.put(request.getId(), payment);
            }

        } else {
            throw new IllegalArgumentException("Unknown gateway: " + request.getGateway());
        }
    }

    private boolean callPayPalAPI(PaymentRequest request, String clientId, String secret) {
        // شبیه‌سازی تماس با PayPal
        System.out.println("Calling PayPal API with clientId: " + clientId);
        return Math.random() > 0.2;
    }

    private boolean callStripeAPI(PaymentRequest request, String apiKey, String webhookSecret) {
        System.out.println("Calling Stripe API with apiKey: " + apiKey);
        return Math.random() > 0.2;
    }

    private boolean callZarinpalAPI(PaymentRequest request, String merchantId, String callbackUrl) {
        System.out.println("Calling Zarinpal API with merchantId: " + merchantId);
        return Math.random() > 0.2;
    }

    private void sendPayPalNotification(PaymentRequest request) {
        System.out.println("PayPal notification sent for: " + request.getId());
    }

    private void sendStripeNotification(PaymentRequest request) {
        System.out.println("Stripe notification sent for: " + request.getId());
    }

    private void sendZarinpalNotification(PaymentRequest request) {
        System.out.println("Zarinpal notification sent for: " + request.getId());
    }
}