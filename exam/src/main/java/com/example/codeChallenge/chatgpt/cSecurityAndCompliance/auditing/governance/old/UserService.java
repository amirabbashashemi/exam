package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.auditing.governance.old;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public void updateUserName(Long userId, String newName) {
        User user = users.get(userId);
        if (user != null) {
            user.setName(newName); // ❌ سابقه تغییر ثبت نمی‌شود
        }
    }

    public void updateUserEmail(Long userId, String newEmail) {
        User user = users.get(userId);
        if (user != null) {
            user.setEmail(newEmail); // ❌ سابقه تغییر ثبت نمی‌شود
        }
    }
}
/*
کد را طوری بازطراحی کنید که:
تمام تغییرات روی User قابل Audit باشند
تغییرات شامل: کاربر عامل (actor)، زمان تغییر، فیلدهای تغییر یافته و مقدار قبلی و جدید باشد
امکان بازخوانی تاریخچه تغییرات برای گزارش و بررسی قانونی وجود داشته باشد
طراحی طوری باشد که افزودن فیلدهای جدید به User یا افزودن قابلیت‌های جدید Audit راحت باشد
 */