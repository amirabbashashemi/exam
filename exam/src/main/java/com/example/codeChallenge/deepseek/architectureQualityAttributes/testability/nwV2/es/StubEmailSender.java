package com.example.codeChallenge.deepseek.architectureQualityAttributes.testability.nwV2.es;

public class StubEmailSender implements EmailSender {
    @Override
    public void sendWelcome(String email) {
        System.out.println("Stub: Sending welcome email to " + email);
    }
}
