package com.example.codeChallenge.deepseek.architectureQualityAttributes.deployability;

/*
 (قابلیت استقرارپذیری) معیاری است برای سنجش سهولت، سرعت و ایمنی فرآیند استقرار

مشکلات:
برای استقرار در محیط Dev، باید کد را تغییر دهید و دوباره کامپایل کنید.
برای استقرار در Test، دوباره کد را تغییر می‌دهید.
هر بار که می‌خواهید محیط را عوض کنید، یک Deploy جدید و کامپایل مجدد نیاز است (هزینه‌ی بالا و ریسک بالا).
هیچ راهی برای تغییر تنظیمات بدون تغییر کد وجود ندارد.
 */
public class NotificationServiceOld {
    public void sendNotification(String to, String message) {
        // تمام تنظیمات به‌صورت هاردکد در کد! (فقط برای Production)
        String smtpHost = "smtp.prod.company.com";
        int smtpPort = 465;
        boolean useTLS = true;
        String logLevel = "WARN";

        System.out.println("Connecting to " + smtpHost + ":" + smtpPort + " (TLS: " + useTLS + ")");
        System.out.println("Log level: " + logLevel);
        System.out.println("Sending to " + to + ": " + message);

        // در واقعیت، اینجا کد ارسال ایمیل واقعی است...
    }


}