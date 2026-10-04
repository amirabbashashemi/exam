package com.example.codeChallenge.deepseek.security.security.me;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
یک سرویس UserAuthenticationService داریم که وظیفه‌اش ثبت‌نام و ورود کاربران است.
 در حال حاضر رمزهای عبور به صورت Plain Text در دیتابیس ذخیره می‌شوند
و هیچ محدودیتی برای تعداد تلاش‌های ناموفق ورود وجود ندارد.
 یک هکر می‌تواند با حملات Brute-Force رمزها را حدس بزند یا با SQL Injection به دیتابیس نفوذ کند.
 مدیرعامل می‌گوید: «امنیت کاربران برای ما حیاتی است.
 رمزها باید به شکل امن ذخیره شوند،
  جلوی حملات Brute-Force گرفته شود،
  و ورودی‌ها قبل از هر پردازشی اعتبارسنجی شوند. همچنین داده‌های حساس در لاگ‌ها ثبت نشوند.»
 */











public class UserAuthenticationService {
    private final int maxLoginAttempt;
    private final PasswordHasher passwordHasher;
    private final ValidationService validationService;
    private final Map<String, Integer> userLoginCountMap = new ConcurrentHashMap<>();

    public UserAuthenticationService(int maxLoginAttempt,
                                     PasswordHasher passwordHasher,
                                     ValidationService validationService) {
        this.maxLoginAttempt = maxLoginAttempt;
        this.passwordHasher = passwordHasher;
        this.validationService = validationService;
    }

    public void register(String username, String password) {
        this.validationService.validate(username, password);

        UserDto userDto = passwordHasher.hashPassword(username, password);

        saveToDatabase(userDto);
    }

    public boolean login(String username, String inputPassword) {
        int count = userLoginCountMap.getOrDefault(username, 0);
        if (count > maxLoginAttempt) {
            throw new RuntimeException("Maximum login count attempt");
        }

        this.validationService.validate(username, inputPassword);

        UserDto storedUserDto = findPasswordFromDatabase(username);

        if (storedUserDto == null) {
            return false;
        }

        boolean validPass = this.passwordHasher.checkPassword(inputPassword, storedUserDto.password());

        if (!validPass) {
            userLoginCountMap.merge(username, 1, Integer::sum);
        } else {
            userLoginCountMap.remove(username);
        }

        return validPass;
    }

    //این متد رو تغییر نمیدو چون شبیه سازی ذخیره در دیتابیس هست
    private void saveToDatabase(UserDto userDto) {
        // ذخیره‌سازی
    }

    //این متد رو تغییر نمیدو چون شبیه سازی خواندن دیتابیس هست
    private UserDto findPasswordFromDatabase(String username) {
        // خواندن از دیتابیس
        return null;
    }

}