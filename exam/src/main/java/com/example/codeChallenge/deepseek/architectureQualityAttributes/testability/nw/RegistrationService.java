package com.example.codeChallenge.deepseek.architectureQualityAttributes.testability.nw;


import codeChallenge.deepseek.architectureQualityAttributes.testability.nwV2.es.EmailSender;
import codeChallenge.deepseek.architectureQualityAttributes.testability.nwV2.re.UserRepository;

/*
سناریوی زیر را در نظر بگیرید. شما یک سرویس ثبت‌نام کاربر (User Registration) دارید که کارهای زیر را انجام می‌دهد:

یک کاربر جدید با username و email ذخیره می‌کند.
یک ایمیل تایید (Welcome Email) برای کاربر می‌فرستد.
تاریخ ثبت‌نام را بر اساس زمان فعلی سیستم ثبت می‌کند.
کد فعلی (کاملاً غیرقابل تست - Legacy):
 */
// ===== سرویس اصلی (غیرقابل تست) =====
public class RegistrationService {
    private final EmailSender emailSender;
    private final UserRepository userRepository;

    public RegistrationService(EmailSender emailSender, UserRepository userRepository) {
        this.emailSender = emailSender;
        this.userRepository = userRepository;
    }

    public void register(String username, String email) {

        // 2. ذخیره در دیتابیس
        userRepository.save(username, email);

        // 3. ارسال ایمیل (تست را کند و غیرقابل پیش‌بینی می‌کند)
        emailSender.sendWelcome(email);

        // 4. استفاده از زمان سیستمی (باعث می‌شود تست همیشه متفاوت باشد)
        long registrationTime = System.currentTimeMillis();
        System.out.println("Registered at: " + registrationTime);

    }


    static void main() {
        EmailSender emailSender = EmailSenderFactory.anInstance(TestModeEnum.MAIN);
//        EmailSender emailSender = EmailSenderFactory.anInstance(TestModeEnum.TEST);

        UserRepository userRepository = UserRepositoryFactory.anInstance(TestModeEnum.MAIN);
//        UserRepository userRepository = UserRepositoryFactory.anInstance(TestModeEnum.TEST);


        RegistrationService registrationService = new RegistrationService(emailSender, userRepository);
        registrationService.register("John", "welcome john :D");
    }
}