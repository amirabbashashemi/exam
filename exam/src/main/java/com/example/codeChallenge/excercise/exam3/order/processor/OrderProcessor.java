package com.example.codeChallenge.excercise.exam3.order.processor;
/*
یک سرویس OrderProcessor داریم که سفارش‌های مشتریان را پردازش می‌کند و در دیتابیس ذخیره می‌کند.
در حال حاضر روزانه ۱۰۰ سفارش پردازش می‌شود و مشکلی وجود ندارد،
اما مدیرعامل می‌گوید در صورت بروز هرگونه خطا (مثلاً قطع ارتباط با دیتابیس یا خرابی دیسک)، نباید هیچ سفارشی از بین برود
 و سیستم باید بتواند پس از ری‌استارت شدن، کار خود را از همان جایی که قطع شده ادامه دهد و
   اگر سفارشی خراب بود، آن را در یک صف جداگانه برای بررسی دستی قرار دهد  و به پردازش بقیه ادامه دهد.
 */
public class OrderProcessor {
    public void process(Order order) {
        // اعتبارسنجی
        if (order.getId() == null) {
            throw new RuntimeException("Invalid order");
        }

        // ذخیره در دیتابیس
        saveToDatabase(order);

        // به‌روزرسانی وضعیت
        updateStatus(order.getId(), "PROCESSED");
    }

    private void saveToDatabase(Order order) {
        // ذخیره‌سازی در دیتابیس
    }

    private void updateStatus(Long orderId, String status) {
        // به‌روزرسانی وضعیت
    }
}