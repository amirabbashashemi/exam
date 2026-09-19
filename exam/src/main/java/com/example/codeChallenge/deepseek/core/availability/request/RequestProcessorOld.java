package com.example.codeChallenge.deepseek.core.availability.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
*مشکلات فعلی:
درخواست‌های سنگین (HEAVY) منابع را اشغال می‌کنند
درخواست‌های سبک (LIGHT) باید سریع پاسخ داده شوند
اگر تعداد درخواست‌های سنگین زیاد شود، درخواست‌های سبک هم کند می‌شوند
هیچ جداسازی بین انواع درخواست‌ها وجود ندارد
یک نوع درخواست می‌تواند کل سیستم را از کار بیندازد
* */
public class RequestProcessorOld {
    private final ExecutorService executor = Executors.newFixedThreadPool(20);
    private final Map<String, List<Request>> history = new ConcurrentHashMap<>();

    public void processRequest(Request request) {
        executor.submit(() -> {
            try {
                // درخواست‌های سنگین (مثل گزارش‌گیری)
                if (request.getType() == RequestType.HEAVY) {
                    Thread.sleep(5000); // ۵ ثانیه پردازش سنگین
                    generateHeavyReport(request);
                }
                // درخواست‌های سبک (مثل وضعیت)
                else if (request.getType() == RequestType.LIGHT) {
                    Thread.sleep(100); // ۱۰۰ میلی‌ثانیه
                    getStatus(request);
                }
                // درخواست‌های معمولی
                else {
                    Thread.sleep(1000); // ۱ ثانیه
                    processNormal(request);
                }

                // ذخیره تاریخچه
                history.computeIfAbsent(request.getUserId(), k -> new ArrayList<>()).add(request);

            } catch (Exception e) {
                System.err.println("Error processing request: " + e.getMessage());
            }
        });
    }

    private void generateHeavyReport(Request request) {

    }

    private void getStatus(Request request) {

    }

    private void processNormal(Request request) {

    }
}
