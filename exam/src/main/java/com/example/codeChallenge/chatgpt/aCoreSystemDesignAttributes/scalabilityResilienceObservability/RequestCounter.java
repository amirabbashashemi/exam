package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.scalabilityResilienceObservability;

import java.util.HashMap;
import java.util.Map;

public class RequestCounter {
    private final Map<String, Integer> counter = new HashMap<>();

    public void increment(String userId) {
        counter.put(userId, counter.getOrDefault(userId, 0) + 1);
    }

    public int getCount(String userId) {
        return counter.getOrDefault(userId, 0);
    }

}

/*
وقتی تعداد کاربران زیاد شود (مثلاً صد هزار کاربر)، متد increment synchronized نیست → race condition
اگر چند thread همزمان increment کنند، count اشتباه می‌شود
امکان مشاهده real-time تعداد درخواست‌ها وجود ندارد
سرویس crash شود → همه داده‌ها از بین می‌رود
Performance برای درخواست‌های concurrent پایین است
 */