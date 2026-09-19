package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.privacy.old;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import codeChallenge.chatgpt.eCommon.UserActivity;

import java.io.IOException;

public class AnalyticsHandler {
    private final Gson gson = new Gson();
    private final AnalyticsRepository repository = new AnalyticsRepository();

    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("POST")) {
            UserActivity activity = gson.fromJson(exchange.getRequestBody().toString(), UserActivity.class);

            repository.save(activity);

            exchange.sendResponseHeaders(200, -1);
        }
    }
}
/*
کد فعلی اصول Privacy را رعایت نمی‌کند و:
داده‌های شخصی کاربران را بدون محدودیت ذخیره می‌کند
داده‌ها بیش از هدف استفاده (Purpose) نگهداری می‌شوند
امکان ناشناس‌سازی (Anonymization) یا حذف داده‌ها وجود ندارد
برای قوانین حفاظت از داده (مثل GDPR) آماده نیست
 */