package com.example.codeChallenge.deepseek.architectureQualityAttributes.interoperability;

/*
Interoperability (قابلیت هم‌کاری) معیاری است برای سنجش سهولت تبادل داده و استفاده از خدمات بین دو یا چند سیستم نرم‌افزاری ناهمگن (با زبان‌های برنامه‌نویسی، فرمت‌های داده، یا پروتکل‌های ارتباطی متفاوت).


سناریوی زیر را در نظر بگیرید. شما یک سرویس پرداخت داخلی دارید که اطلاعات تراکنش را در قالب یک کلاس جاوا (POJO) نگهداری می‌کند. این داده‌ها باید به دو سیستم خارجی مختلف ارسال شوند:
سیستم حسابداری قدیمی (Legacy Accounting System): فقط داده‌ها را به‌صورت یک رشته‌ی متنی با جداکننده‌ی | (Pipe) قبول می‌کند:
مثال: "TXN-123|250.0|USD|Payment for invoice #456"
سیستم گزارش‌گیری مدرن (Modern Reporting System): داده‌ها را به‌صورت CSV (با جداکننده‌ی کاما) قبول می‌کند:
مثال: "TXN-123,250.0,USD,Payment for invoice #456"
 */
public class PaymentServiceOld {
    public void processPayment(PaymentTransaction tx) {
        // 1. ارسال به سیستم حسابداری قدیمی
        String legacyFormat = tx.id + "|" + tx.amount + "|" + tx.currency + "|" + tx.description;
        System.out.println("Sending to Legacy System: " + legacyFormat);

        // 2. ارسال به سیستم گزارش‌گیری مدرن
        String modernFormat = tx.id + "," + tx.amount + "," + tx.currency + "," + tx.description;
        System.out.println("Sending to Reporting System: " + modernFormat);
    }

    // کلاس داده (POJO)
    class PaymentTransaction {
        String id;
        double amount;
        String currency;
        String description;
    }
}