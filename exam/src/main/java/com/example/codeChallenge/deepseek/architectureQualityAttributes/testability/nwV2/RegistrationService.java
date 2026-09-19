package com.example.codeChallenge.deepseek.architectureQualityAttributes.testability.nwV2;

import codeChallenge.deepseek.architectureQualityAttributes.testability.nwV2.es.EmailSender;
import codeChallenge.deepseek.architectureQualityAttributes.testability.nwV2.es.StubEmailSender;
import codeChallenge.deepseek.architectureQualityAttributes.testability.nwV2.re.StubUserRepository;
import codeChallenge.deepseek.architectureQualityAttributes.testability.nwV2.re.UserRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

/*
سناریوی زیر را در نظر بگیرید. شما یک سرویس ثبت‌نام کاربر (User Registration) دارید که کارهای زیر را انجام می‌دهد:

یک کاربر جدید با username و email ذخیره می‌کند.
یک ایمیل تایید (Welcome Email) برای کاربر می‌فرستد.
تاریخ ثبت‌نام را بر اساس زمان فعلی سیستم ثبت می‌کند.
کد فعلی (کاملاً غیرقابل تست - Legacy):
 */
// ===== سرویس اصلی (غیرقابل تست) =====
public class RegistrationService {
    private final Clock clock;
    private final EmailSender emailSender;
    private final UserRepository userRepository;

    public RegistrationService(Clock clock, EmailSender emailSender, UserRepository userRepository) {
        this.clock = clock;
        this.emailSender = emailSender;
        this.userRepository = userRepository;
    }

    public void register(String username, String email) {
        // 2. ذخیره در دیتابیس
        userRepository.save(username, email);

        // 3. ارسال ایمیل (تست را کند و غیرقابل پیش‌بینی می‌کند)
        emailSender.sendWelcome(email);

        // 4. استفاده از زمان سیستمی (باعث می‌شود تست همیشه متفاوت باشد)
        long registrationTime = clock.millis();
        System.out.println("Registered at: " + registrationTime);
    }

    static void main(String[] args) {
        Clock clock = Clock.fixed(Instant.parse("2026-08-07T07:00:00"), ZoneOffset.UTC);
        EmailSender stubSender = new StubEmailSender();
        UserRepository stubRepo = new StubUserRepository();

        RegistrationService service = new RegistrationService(clock, stubSender, stubRepo);
        service.register("alice", "alice@example.com");

        // خروجی قابل‌پیش‌بینی: زمان ثابت است.
        // تست را می‌توان به‌راحتی با assert بررسی کرد (اگر خروجی را String بگیرید).
    }
}