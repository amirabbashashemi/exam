package com.example.codeChallenge.deepseek.architectureQualityAttributes.portability;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

/*
سناریوی زیر را در نظر بگیرید. شما یک سرویس گزارش‌گیری (Report Generator) دارید که کارهای زیر را انجام می‌دهد:
یک فایل قالب (Template) را از مسیر /opt/app/template/report.tpl (در لینوکس) می‌خواند.
یک فایل خروجی را در مسیر /var/log/app/report_YYYYMMDD.txt می‌نویسد.
برای اطمینان از وجود پوشه‌ی خروجی، از دستور mkdir -p در لینوکس استفاده می‌کند (از طریق Runtime.exec).
برای چاپ تاریخ، از new Date() با فرمت پیش‌فرض استفاده می‌کند که به Locale سیستم وابسته است.
 */

public class ReportGenerator {
    private final Path templatePath;
    private final Path outputDir;
    private final Clock clock;

    public ReportGenerator(Path templatePath, Path outputDir, Clock clock) {
        this.templatePath = templatePath;
        this.outputDir = outputDir;
        this.clock = clock;
    }

    public void generateReport(String content) {
        try {
            // 1. ایجاد پوشه‌ی خروجی (به‌صورت portable)
            Files.createDirectories(outputDir);

            // 2. خواندن فایل قالب (با UTF-8)
            String templateFile = readFile();

            // 3. تولید نام فایل با تاریخ ثابت (مستقل از Locale)
            String dateStr = LocalDate.now(clock).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            Path outputFile = outputDir.resolve("report_" + dateStr + ".txt");

            // 4. نوشتن فایل خروجی (با UTF-8)
            writeFile(outputFile, templateFile, content);

            System.out.println("Report generated at: " + outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readFile() throws IOException {
        try {
            String templateLine = "";

            if (Files.exists(templatePath)) {
                List<String> strings = Files.readAllLines(templatePath, StandardCharsets.UTF_8);

                templateLine = strings.stream()
                        .filter(line -> !line.isEmpty())
                        .findFirst()
                        .orElseThrow(() -> new IOException("Error in method readFile "));
            } else {
                throw new FileNotFoundException("Error in method path not found: " + templatePath);
            }
            return templateLine;
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new IOException("file " + templatePath + " not exists. " + e.getMessage());
        }

    }

    private void writeFile(Path outputFile, String template, String content) {
        try {
            String finalContent = template + System.lineSeparator() + content;
            Files.writeString(outputFile, finalContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("IOException in method writeFile: " + e.getMessage());
        }
    }

}