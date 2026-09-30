package com.example.codeChallenge.excercise.exam3.observability;

public class OrderProcessingService {
    public void process(Order order) {
        // اعتبارسنجی
        if (order.getId() == null) {
            throw new RuntimeException("Invalid order");
        }

        // ذخیره در دیتابیس
        saveToDatabase(order);

        // ارسال اعلان
        sendNotification(order);
    }

    private void saveToDatabase(Order order) {
        // ذخیره‌سازی
    }

    private void sendNotification(Order order) {
        // ارسال اعلان
    }
}