package com.example.codeChallenge.excercise.exam3.elasticity.performance.availability.image;
/*
یک سرویس Image Processing Service داریم که تصاویر آپلودشده توسط کاربران را پردازش می‌کند.
این سرویس باید در برابر بار سنگین مقاوم باشد، درخواست‌های طولانی را کنسل کند، و در صورت خرابی، در دسترس بماند.
 */
public class ImageProcessingService {

    public void process(String imageId, byte[] imageData) {
        // پردازش سنگین تصویر (شبیه‌سازی)
        byte[] processed = heavyProcessing(imageData);
        saveToStorage(imageId, processed);
    }

    private byte[] heavyProcessing(byte[] data) {
        // شبیه‌سازی پردازش سنگین
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        return data;
    }

    private void saveToStorage(String imageId, byte[] data) {
        // ذخیره‌سازی
    }




}