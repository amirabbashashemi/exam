package com.example.codeChallenge.deepseek.architectureQualityAttributes.testability;

import java.util.HashMap;
import java.util.Map;

/*
سناریوی زیر را در نظر بگیرید. شما یک سرویس ثبت‌نام کاربر (User Registration) دارید که کارهای زیر را انجام می‌دهد:

یک کاربر جدید با username و email ذخیره می‌کند.
یک ایمیل تایید (Welcome Email) برای کاربر می‌فرستد.
تاریخ ثبت‌نام را بر اساس زمان فعلی سیستم ثبت می‌کند.
کد فعلی (کاملاً غیرقابل تست - Legacy):
 */
// ===== سرویس اصلی (غیرقابل تست) =====
public class RegistrationServiceOld {
    public void register(String username, String email) {
        // 1. وابستگی‌ها را داخل متد new می‌کند (سم کشنده!)
        EmailSender emailSender = new EmailSender();
        UserRepository userRepository = new UserRepository();

        // 2. ذخیره در دیتابیس
        userRepository.save(username, email);

        // 3. ارسال ایمیل (تست را کند و غیرقابل پیش‌بینی می‌کند)
        emailSender.sendWelcome(email);

        // 4. استفاده از زمان سیستمی (باعث می‌شود تست همیشه متفاوت باشد)
        long registrationTime = System.currentTimeMillis();
        System.out.println("Registered at: " + registrationTime);
    }

    // ===== EmailSender (ارسال کننده ایمیل واقعی) =====
    class EmailSender {
        public void sendWelcome(String email) {
            // در واقعیت به سرور SMTP متصل می‌شود
            System.out.println("Sending welcome email to " + email);
            // اتصال به شبکه، ممکن است ۱-۲ ثانیه طول بکشد!
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
        }
    }

    // ===== UserRepository (ذخیره در دیتابیس) =====
    class UserRepository {
        private final Map<String, String> store = new HashMap<>();

        public void save(String username, String email) {
            store.put(username, email);
            System.out.println("Saved to DB: " + username);
            // ممکن است خطای Connection پرتاب کند!
        }
    }

}