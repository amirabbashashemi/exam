package com.example.codeChallenge.excercise.exam3.security;

/*
یک سرویس UserAuthenticationService داریم که وظیفه‌اش ثبت‌نام و ورود کاربران است. در حال حاضر رمزهای عبور به صورت Plain Text در دیتابیس ذخیره می‌شوند
و هیچ محدودیتی برای تعداد تلاش‌های ناموفق ورود وجود ندارد. یک هکر می‌تواند با حملات Brute-Force رمزها را حدس بزند یا با SQL Injection به دیتابیس نفوذ کند.
 مدیرعامل می‌گوید: «امنیت کاربران برای ما حیاتی است. رمزها باید به شکل امن ذخیره شوند، جلوی حملات Brute-Force گرفته شود،
  و ورودی‌ها قبل از هر پردازشی اعتبارسنجی شوند. همچنین داده‌های حساس در لاگ‌ها ثبت نشوند.»
 */
public class UserAuthenticationService {
    public void register(String username, String password) {
        // ذخیره رمز به صورت Plain Text
        saveToDatabase(username, password);
    }

    public boolean login(String username, String password) {
        String storedPassword = findPasswordFromDatabase(username);
        return password.equals(storedPassword);
    }

    private void saveToDatabase(String username, String password) {
        // ذخیره‌سازی
    }

    private String findPasswordFromDatabase(String username) {
        // خواندن از دیتابیس
        return "";
    }
}