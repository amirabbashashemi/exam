package com.example.codeChallenge.deepseek.architectureQualityAttributes.testability.nwV2.es;

public class RealEmailSender implements EmailSender {
    @Override
    public void sendWelcome(String email) {
        // در واقعیت به سرور SMTP متصل می‌شود
        System.out.println("Sending welcome email to " + email);
        // اتصال به شبکه، ممکن است ۱-۲ ثانیه طول بکشد!
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }
}
