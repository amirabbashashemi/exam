package com.example.codeChallenge.deepseek.structural.localization;

/*
 (بومی‌سازی) localization:
به معنای قابلیت تطبیق برنامه با زبان، فرهنگ و قواعد منطقه‌ای خاص (Locale) بدون تغییر


درخواست جدید محصول:
فروشگاه به بازارهای فرانسه، آلمان و ژاپن وارد شده است. نیاز داریم:
پیام‌های فاکتور به زبان کاربر (مثلاً فرانسوی، آلمانی، ژاپنی) نمایش داده شوند.
تاریخ به‌صورت استاندارد آن کشور (مثلاً در فرانسه: 04/08/2026 و در آلمان: 04.08.2026) نمایش داده شود.
مبلغ با ارز و فرمت آن کشور (مثلاً در فرانسه: 1 250,50 € و در آلمان: 1.250,50 €) نمایش داده شود.
کد باید طوری نوشته شود که افزودن زبان/کشور جدید (مثلاً اسپانیایی) در آینده، فقط با اضافه کردن یک فایل Properties جدید امکان‌پذیر باشد و نیازی به تغییر کد جاوا نداشته باشد.
 */

import java.util.Date;

public class InvoiceServiceOld {
    public String generateInvoice(String customerName, double amount, Date orderDate) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== INVOICE ===\n");
        sb.append("Customer: ").append(customerName).append("\n");
        sb.append("Order Date: ").append(orderDate.toString()).append("\n"); // مشکل ۱
        sb.append("Total Amount: ").append("$").append(amount).append("\n"); // مشکل ۲
        sb.append("Thank you for your purchase!");
        return sb.toString();
    }

    public static void main(String[] args) {
        InvoiceServiceOld service = new InvoiceServiceOld();
        // تست با یک مشتری فرانسوی (باید پیام به فرانسوی باشد و تاریخ/ارز به سبک فرانسه)
        String invoice = service.generateInvoice("Jean Dupont", 1250.5, new Date());
        System.out.println(invoice);
    }
}