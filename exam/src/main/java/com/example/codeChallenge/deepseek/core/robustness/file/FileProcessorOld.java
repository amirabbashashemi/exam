package com.example.codeChallenge.deepseek.core.robustness.file;

import java.io.*;
import java.nio.file.*;
import java.util.*;
/*
 (استحکام/مقاومت در برابر خطا)
مشکلات فعلی:
اگر فایل وجود نداشته باشد، برنامه کرش می‌کند (NullPointerException)
اگر فرمت خطا باشد (مثلاً تعداد ستون‌ها کم باشد)، برنامه کرش می‌کند
اگر داده‌ها معتبر نباشند (مثلاً age عدد نباشد)، برنامه کرش می‌کند
اگر خطا رخ دهد، فایل‌های قبلی پردازش شده از دست می‌روند
هیچ گزارشی از خطاها وجود ندارد
اگر فایل خالی باشد، خروجی خالی ایجاد می‌شود
 */
public class FileProcessorOld {
    private final String inputDir = "./input/";
    private final String outputDir = "./output/";
    private final List<String> processedFiles = new ArrayList<>();

    public void processAllFiles() throws IOException {
        File[] files = new File(inputDir).listFiles((d, name) -> name.endsWith(".csv"));

        for (File file : files) {
            processFile(file);
        }
    }

    private void processFile(File file) throws IOException {
        System.out.println("Processing: " + file.getName());

        // خواندن فایل
        List<String> lines = Files.readAllLines(file.toPath());

        // پردازش هر خط
        List<String> results = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(",");

            // تبدیل داده
            String name = parts[0];
            int age = Integer.parseInt(parts[1]);
            double salary = Double.parseDouble(parts[2]);

            String result = String.format("%s,%d,%.2f", name.toUpperCase(), age + 5, salary * 1.1);
            results.add(result);
        }

        // ذخیره خروجی
        String outputName = file.getName().replace(".csv", ".out");
        Files.write(Paths.get(outputDir, outputName), results);

        processedFiles.add(file.getName());
    }
}