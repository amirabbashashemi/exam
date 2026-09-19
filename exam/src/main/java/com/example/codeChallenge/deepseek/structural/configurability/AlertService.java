package com.example.codeChallenge.deepseek.structural.configurability;


public class AlertService {
    // مقادیر Hard-Coded
    private final AlertConfiguration alertConfiguration;

    public AlertService() {
        alertConfiguration = new AlertConfiguration();
    }

    public AlertService(AlertConfiguration alertConfiguration) {
        this.alertConfiguration = alertConfiguration;
    }

    public void sendAlert(String message, Throwable error) {
        String logLevel = alertConfiguration.getLogLevel();
        boolean enableEmailAlerts = alertConfiguration.isEnableEmailAlerts();
        int maxRetryCount = alertConfiguration.getMaxRetryCount();
        int retryDelayMS = alertConfiguration.getRetryDelayMS();
        String alertRecipient = alertConfiguration.getAlertRecipient();

        if (logLevel.equals("DEBUG")) {
            System.out.println("[DEBUG] " + message);
        } else if (logLevel.equals("INFO")) {
            System.out.println("[INFO] " + message);
        }

        if (enableEmailAlerts) {
            for (int i = 0; i < maxRetryCount; i++) {
                try {
                    System.out.println("Sending email to " + alertRecipient + " : " + message);
                    // شبیه‌سازی ارسال ایمیل
                    if (error != null) {
                        // خطای شبیه‌سازی شده برای بررسی Retry
                        if (i < maxRetryCount - 1) {
                            System.out.println("Retry attempt " + (i + 1) + " failed, waiting " + retryDelayMS + "ms");
                            Thread.sleep(retryDelayMS);
                        } else {
                            System.out.println("Alert sent successfully after retries.");
                        }
                    }
                    break;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } else {
            System.out.println("Email alerts are disabled. Alert logged locally.");
        }
    }

    public static void main(String[] args) {
        AlertService service = new AlertService();
        service.sendAlert("Database connection lost!", new RuntimeException("DB Timeout"));
    }
}