package com.example.codeChallenge.deepseek.architectureQualityAttributes.deployability;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 (قابلیت استقرارپذیری) معیاری است برای سنجش سهولت، سرعت و ایمنی فرآیند استقرار

مشکلات:
برای استقرار در محیط Dev، باید کد را تغییر دهید و دوباره کامپایل کنید.
برای استقرار در Test، دوباره کد را تغییر می‌دهید.
هر بار که می‌خواهید محیط را عوض کنید، یک Deploy جدید و کامپایل مجدد نیاز است (هزینه‌ی بالا و ریسک بالا).
هیچ راهی برای تغییر تنظیمات بدون تغییر کد وجود ندارد.
 */
public class NotificationService {
    private final String defaultConfigUrl = "/smtp.config";
    private final Properties properties = new Properties();

    public NotificationService() {
        initiateMap();
    }

    private void initiateMap() {
        try {
            String configFileUrl = getConfigFileUrl();
            InputStream resourceAsStream = this.getClass().getResourceAsStream(configFileUrl);
            if (resourceAsStream == null) {
                throw new RuntimeException("Config file not found: " + configFileUrl);
            }
            properties.load(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException("IOException in method initiateMap: " + e.getMessage());
        }
    }

    private String getConfigFileUrl() {
        String url = System.getProperty("config_url");
        if (url == null) {
            url = System.getenv("config_url");
        }

        if (url == null) {
            url = defaultConfigUrl;
        }

        return url;
    }

    public void sendNotification(String to, String message) {
        // تمام تنظیمات به‌صورت هاردکد در کد! (فقط برای Production)
        String smtpHost = properties.getProperty("smtpHost");
        String logLevel = properties.getProperty("logLevel");
        String smtpPortStr = properties.getProperty("smtpPort");
        String useTLSStr = properties.getProperty("useTLS");

        int smtpPort = Integer.valueOf(smtpPortStr);
        boolean useTLS = Boolean.valueOf(useTLSStr);

        System.out.println("Connecting to " + smtpHost + ":" + smtpPort + " (TLS: " + useTLS + ")");
        System.out.println("Log level: " + logLevel);
        System.out.println("Sending to " + to + ": " + message);

        // در واقعیت، اینجا کد ارسال ایمیل واقعی است...
    }

}