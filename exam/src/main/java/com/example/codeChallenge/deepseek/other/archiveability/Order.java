package com.example.codeChallenge.deepseek.other.archiveability;

/*
تعریف جامع Archiveability در سطح معماری نرم‌افزار
Archiveability (قابلیت بایگانی یا بایگانی‌پذیری) معیاری است برای سنجش سهولت، کارایی و ایمنی انتقال داده‌های قدیمی یا غیرفعال از سیستم اصلی (Online/Production) به یک سیستم ذخیره‌سازی بلندمدت (Archive/Data Lake).
در سطح معماری، یک سیستم با Archiveability بالا یعنی:
جداسازی داده‌های فعال از غیرفعال: داده‌های تاریخی که دیگر به‌طور روزمره استفاده نمی‌شوند، به‌راحتی و بدون تأثیر بر عملکرد سیستم اصلی، به سیستم بایگانی منتقل می‌شوند.
قابلیت بازیابی (Retrievability): داده‌های بایگانی‌شده باید در صورت نیاز (مثلاً برای گزارش‌گیری یا ممیزی) قابل بازیابی و جستجو باشند.
مدیریت چرخه‌ی حیات داده (Data Lifecycle Management): سیستم باید سیاست‌های مشخصی برای بایگانی (مثلاً داده‌های قدیمی‌تر از ۳ سال) و حذف نهایی (Purge) داشته باشد.
عدم تأثیر بر عملکرد (Performance Isolation): فرآیند بایگانی نباید بر عملکرد سیستم اصلی تأثیر بگذارد (معمولاً با استفاده از Jobهای زمان‌بندی‌شده در ساعات غیرکاری).
قابلیت جابجایی (Portability): داده‌های بایگانی باید بتوانند به‌سادگی بین محیط‌های مختلف (مثلاً از دیتابیس اصلی به S3 یا HDFS) منتقل شوند.
 */

import java.time.LocalDate;

public class Order {
    private final String id;
    private final LocalDate orderDate;
    private final double amount;
    private String status; // "COMPLETED", "PENDING", "CANCELLED" , "ARCHIVED"

    public Order(String id, LocalDate orderDate, double amount, String status) {
        this.id = id;
        this.orderDate = orderDate;
        this.amount = amount;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
