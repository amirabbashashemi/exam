package com.example.codeChallenge.deepseek.architectureQualityAttributes.testability.nw.email;

import codeChallenge.deepseek.architectureQualityAttributes.testability.nw.TestModeEnum;

public class EmailSenderMockImpl implements EmailSender {

    @Override
    public TestModeEnum type() {
        return TestModeEnum.TEST;
    }

    @Override
    public void sendWelcome(String email) {
        System.out.println("Mock: Sending welcome email to " + email);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.err.printf("Mock: InterruptedException in method sendWelcome: %s", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
