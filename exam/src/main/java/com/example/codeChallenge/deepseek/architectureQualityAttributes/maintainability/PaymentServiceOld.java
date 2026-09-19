package com.example.codeChallenge.deepseek.architectureQualityAttributes.maintainability;
/*
کد بالا را با رعایت دقیق Maintainability بازنویسی کنید. الزامات معماری:

افزودن درگاه جدید (مثلاً CryptoCurrency) فقط نیازمند ایجاد یک کلاس جدید باشد و هیچ تغییری در کلاس‌های موجود ایجاد نکند (اصل Open/Closed).

افزودن عملیات جدید (مثلاً void یا cancel) نیز فقط نیازمند تغییر در یک نقطه‌ی مشخص باشد و به سایر درگاه‌ها آسیبی نزند.

مسئولیت‌ها کاملاً تفکیک شوند (Single Responsibility).

از Java 21 Standard Library استفاده کنید. هیچ فریم‌ورک یا کتابخانه‌ی خارجی مجاز نیست.

نیازی به پیاده‌سازی کامل منطق تجاری (مثل اتصال به بانک) نیست. فقط ساختار (Structure) و ارتباط بین کلاس‌ها را با متدهای نمونه (Placeholder) نشان دهید.
 */
public class PaymentServiceOld {
    public void process(String method, String action, double amount, String credential) {
        if (method.equals("CreditCard")) {
            if (action.equals("authorize")) {
                System.out.println("Auth CC: " + credential + " for " + amount);
                // منطق سنگین احراز کارت
            } else if (action.equals("capture")) {
                System.out.println("Cap CC: " + amount);
                // منطق سنگین تسویه
            } else if (action.equals("refund")) {
                System.out.println("Ref CC: " + amount);
                // منطق سنگین بازگشت وجه
            }
        } else if (method.equals("PayPal")) {
            if (action.equals("authorize")) {
                System.out.println("Auth PP with email: " + credential);
            } else if (action.equals("capture")) {
                System.out.println("Cap PP: " + amount);
            } else if (action.equals("refund")) {
                System.out.println("Ref PP: " + amount);
            }
        } else if (method.equals("Stripe")) {
            // دقیقاً همین ساختار تو در تو تکرار شده
        }
    }
}