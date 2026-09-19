package com.example.codeChallenge.deepseek.architectureQualityAttributes.modifiability;

/*

Modifiability (قابلیت تغییرپذیری) = هزینه (زمان و نیرو) و ریسک اعمال یک تغییر خاص در سیستم، با تأکید بر
زمان اجرا (Runtime) و پیکربندی‌های پویا. یعنی سیستم چقدر انعطاف‌پذیر است که بتوان بدون توقف یا بدون کامپایل مجدد، رفتار آن را عوض کرد.




کد بالا را با رعایت اصول Modifiability بازنویسی کنید. نیازهای جدید:
تغییرپذیری در زمان اجرا (Runtime): نرخ تخفیف برای هر نوع کاربر باید از یک Map<String, Double> به نام discountConfig خوانده شود. این Map ممکن است توسط یک Thread دیگر (مثلاً یک Admin Panel) در حین اجرای برنامه به‌روزرسانی شود (شما فقط متد refreshConfig(Map<String, Double> newConfig) را پیاده‌سازی کنید).
قابلیت افزودن نوع کاربر جدید (مثلاً Student) بدون تغییر در کلاس DiscountService و بدون استفاده از if-else یا switch. (استفاده از Strategy Pattern الزامی است).
مدیریت خطا: اگر نوع کاربری در Map وجود نداشت، باید یک تخفیف پیش‌فرض (مثلاً ۰٪) اعمال شود و خطایی پرتاب نشود (سیستم باید مقاوم باشد).
محدودیت: فقط از Java 21 Standard Library استفاده کنید. هیچ کتابخانه‌ی خارجی مجاز نیست.
نکته‌ی طلایی برای Modifiability: برای تغییر نرخ تخفیف VIP از ۲۰٪ به ۲۵٪، فقط کافی است discountConfig را به‌روزرسانی کنیم و متد refreshConfig را صدا بزنیم. هیچ تغییری در کد منبع و هیچ Deploy جدیدی نیاز نباشد.
 */

// کد فعلی (غیرقابل تغییر در زمان اجرا)
public class DiscountServiceOld {
    public double calculate(String userType, double amount) {
        if (userType.equals("VIP")) {
            return amount * 0.8; // 20% تخفیف
        } else if (userType.equals("Employee")) {
            return amount * 0.85; // 15% تخفیف
        } else if (userType.equals("Regular")) {
            return amount * 0.95; // 5% تخفیف
        }
        return amount;
    }
}