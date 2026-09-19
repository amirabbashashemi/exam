package com.example.codeChallenge.deepseek.architectureQualityAttributes.portability;

import java.io.*;
import java.util.Date;
/*
سناریوی زیر را در نظر بگیرید. شما یک سرویس گزارش‌گیری (Report Generator) دارید که کارهای زیر را انجام می‌دهد:

یک فایل قالب (Template) را از مسیر /opt/app/template/report.tpl (در لینوکس) می‌خواند.

یک فایل خروجی را در مسیر /var/log/app/report_YYYYMMDD.txt می‌نویسد.

برای اطمینان از وجود پوشه‌ی خروجی، از دستور mkdir -p در لینوکس استفاده می‌کند (از طریق Runtime.exec).

برای چاپ تاریخ، از new Date() با فرمت پیش‌فرض استفاده می‌کند که به Locale سیستم وابسته است.
 */
public class ReportGeneratorOld {
    public void generateReport(String content) throws Exception {
        // 1. مسیرهای هاردکد (فقط لینوکس)
        String templatePath = "/opt/app/template/report.tpl";
        String outputDir = "/var/log/app/";
        String outputFile = outputDir + "report_" + new Date().toString().replace(" ", "_") + ".txt";

        // 2. اجرای دستور سیستمی برای ساخت پوشه (فقط لینوکس)
        Runtime.getRuntime().exec(new String[]{"mkdir", "-p", outputDir});

        // 3. خواندن فایل قالب (با encoding پیش‌فرض سیستم)
        BufferedReader reader = new BufferedReader(new FileReader(templatePath));
        String template = reader.readLine();
        reader.close();

        // 4. نوشتن فایل خروجی (با encoding پیش‌فرض سیستم)
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(template + "\n" + content);
        writer.close();

        System.out.println("Report generated at: " + outputFile);
    }
}