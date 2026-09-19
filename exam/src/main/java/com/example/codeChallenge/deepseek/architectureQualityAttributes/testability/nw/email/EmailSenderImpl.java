package com.example.codeChallenge.deepseek.architectureQualityAttributes.testability.nw.email;

import codeChallenge.deepseek.architectureQualityAttributes.testability.nw.TestModeEnum;

public class EmailSenderImpl implements EmailSender {

    @Override
    public TestModeEnum type() {
        return TestModeEnum.MAIN;
    }

    @Override
    public void sendWelcome(String email) {
        // در واقعیت به سرور SMTP متصل می‌شود
        System.out.println("Sending welcome email to " + email);
        // اتصال به شبکه، ممکن است ۱-۲ ثانیه طول بکشد!
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.err.printf("InterruptedException in method sendWelcome: %s", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
