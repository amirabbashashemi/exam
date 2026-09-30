package com.example.codeChallenge.excercise.exam3.resilience;
/*
یک سرویس PaymentGatewayService داریم که پرداخت‌های کاربران را از طریق یک API خارجی (بانک) پردازش می‌کند.
 این API گاهی کند می‌شود، گاهی خطای موقتی می‌دهد (مثل Timeout یا ۵۰۳)، و گاهی هم کاملاً از کار می‌افتد.
  در حال حاضر هر بار که API خارجی خطا می‌دهد، برنامه کرش می‌کند یا کاربر باید دستی دوباره تلاش کند.
   مدیرعامل می‌گوید: "اگر بانک خراب است، نباید کل سیستم ما از کار بیفتد.
    باید خودت را با شرایط وفق بدهی و اگر یک درخواست شکست خورد، به‌طور هوشمندانه دوباره تلاش کنی.
     همچنین اگر بانک برای مدت طولانی خراب بود، نباید درخواست‌های جدید را به آن بفرستی تا وقتی که بهبود یابد."
 */
public class PaymentGatewayService {
    public PaymentResult processPayment(PaymentRequest request) {
        // فراخوانی API خارجی
        return callExternalBankApi(request);
    }

    private PaymentResult callExternalBankApi(PaymentRequest request) {
        // شبیه‌سازی فراخوانی API
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        return new PaymentResult(request.getId(), "SUCCESS");
    }
}