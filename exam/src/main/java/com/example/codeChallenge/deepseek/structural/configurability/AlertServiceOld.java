package com.example.codeChallenge.deepseek.structural.configurability;
/*

درخواست جدید محصول:
تیم عملیات (DevOps) اعلام کرده است که در محیط‌های مختلف (توسعه، تست، تولید) نیاز به تنظیمات متفاوتی دارند:
در محیط توسعه: LOG_LEVEL=DEBUG و ENABLE_EMAIL_ALERTS=false
در محیط تولید: LOG_LEVEL=ERROR، ENABLE_EMAIL_ALERTS=true، MAX_RETRY_COUNT=5 و RETRY_DELAY_MS=2000
همچنین ممکن است در آینده پارامترهای جدیدی مانند SLACK_WEBHOOK_URL یا SMS_ENABLED اضافه شوند.
تیم فعلی برای تغییر هر کدام از این مقادیر، مجبور است کد را تغییر دهد، کامپایل کند و اپلیکیشن را دوباره راه‌اندازی (Restart) کند که بسیار هزینه‌بر و زمان‌بر است.
 */
public class AlertServiceOld {
    // مقادیر Hard-Coded
    private static final String LOG_LEVEL = "INFO";
    private static final boolean ENABLE_EMAIL_ALERTS = true;
    private static final int MAX_RETRY_COUNT = 3;
    private static final long RETRY_DELAY_MS = 1000;
    private static final String ALERT_RECIPIENT = "admin@company.com";

    public void sendAlert(String message, Throwable error) {
        if (LOG_LEVEL.equals("DEBUG")) {
            System.out.println("[DEBUG] " + message);
        } else if (LOG_LEVEL.equals("INFO")) {
            System.out.println("[INFO] " + message);
        }

        if (ENABLE_EMAIL_ALERTS) {
            for (int i = 0; i < MAX_RETRY_COUNT; i++) {
                try {
                    System.out.println("Sending email to " + ALERT_RECIPIENT + " : " + message);
                    // شبیه‌سازی ارسال ایمیل
                    if (error != null) {
                        // خطای شبیه‌سازی شده برای بررسی Retry
                        if (i < MAX_RETRY_COUNT - 1) {
                            System.out.println("Retry attempt " + (i + 1) + " failed, waiting " + RETRY_DELAY_MS + "ms");
                            Thread.sleep(RETRY_DELAY_MS);
                        } else {
                            System.out.println("Alert sent successfully after retries.");
                        }
                    }
                    break;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } else {
            System.out.println("Email alerts are disabled. Alert logged locally.");
        }
    }

    public static void main(String[] args) {
        AlertServiceOld service = new AlertServiceOld();
        service.sendAlert("Database connection lost!", new RuntimeException("DB Timeout"));
    }
}