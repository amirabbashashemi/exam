package com.example.codeChallenge.deepseek.architectureQualityAttributes.testability.nw.factory;

import codeChallenge.deepseek.architectureQualityAttributes.testability.nw.TestModeEnum;
import codeChallenge.deepseek.architectureQualityAttributes.testability.nw.email.EmailSender;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class EmailSenderFactory {
    private static final Map<TestModeEnum, EmailSender> emailSenders = new HashMap<>();

    public EmailSenderFactory() {
        ServiceLoader<EmailSender> emailSenders = ServiceLoader.load(EmailSender.class);

        emailSenders.forEach(emailSender -> this.emailSenders.put(emailSender.type(), emailSender));
    }

    public static EmailSender anInstance(TestModeEnum testModeEnum) {
        EmailSender emailSender = emailSenders.get(testModeEnum);
        if (emailSender == null) {
            throw new IllegalArgumentException(testModeEnum.name() + " not supported");
        }
        return emailSender;
    }
}
