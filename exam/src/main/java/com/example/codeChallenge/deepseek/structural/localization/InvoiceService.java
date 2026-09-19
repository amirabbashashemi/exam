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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class InvoiceService {
    private final Locale LOCALE;
    private final ResourceBundle resourceBundle;

    public InvoiceService(Locale locale) {
        LOCALE = locale;
        resourceBundle = ResourceBundle.getBundle("Messages", locale);
    }

    public String generateInvoice(String customerName, double amount, Date orderDate) {
        String title = resourceBundle.getString("title");
        String customer = resourceBundle.getString("customer");
        String tanks = resourceBundle.getString("tanks");

        String date = resourceBundle.getString("date");
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, LOCALE);
        String dateFormatted = dateFormat.format(orderDate);

        String totalAmount = resourceBundle.getString("totalAmount");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(LOCALE);
        String numberFormatted = numberFormat.format(amount);

        return title + "\n" + customer + "\n" + (date + dateFormatted) + "\n" + (totalAmount + numberFormatted) + "\n" + tanks + "\n";
    }

    static void main(String[] args) {
        InvoiceService frService = new InvoiceService(Locale.FRANCE);

        System.out.println(frService.generateInvoice("Jean Dupont", 1250.5, new Date()));

        System.out.println("---");
    }
}