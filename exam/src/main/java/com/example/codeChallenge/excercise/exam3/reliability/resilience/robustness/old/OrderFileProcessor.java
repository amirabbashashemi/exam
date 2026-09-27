package com.example.codeChallenge.excercise.exam3.reliability.resilience.robustness.old;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
/*
یک سرویس Order File Processor داریم که فایل‌های سفارش را از یک دایرکتوری ورودی می‌خواند، پردازش می‌کند و در دایرکتوری خروجی می‌نویسد.

مشکلات فعلی:
اگر در حین پردازش خطایی رخ دهد، برنامه کرش می‌کند.
اگر برنامه وسط کار بسته شود، هیچ نشانی از فایل‌های پردازش‌شده ندارد و دوباره همه را از اول پردازش می‌کند.
هیچ اعتبارسنجی روی محتوای فایل انجام نمی‌شود.
 */
public class OrderFileProcessor {
    private final Path inputDir;
    private final Path outputDir;

    public OrderFileProcessor(Path inputDir, Path outputDir) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
    }

    public void processAll() throws IOException {
        try (Stream<Path> files = Files.list(inputDir)) {
            for (Path file : files.toList()) {
                String content = Files.readString(file);
                // پردازش ساده: تبدیل به حروف بزرگ
                String processed = content.toUpperCase();
                Files.writeString(outputDir.resolve(file.getFileName()), processed);
            }
        }
    }
}