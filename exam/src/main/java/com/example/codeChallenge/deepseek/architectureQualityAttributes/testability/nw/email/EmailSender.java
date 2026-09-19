package com.example.codeChallenge.deepseek.architectureQualityAttributes.testability.nw.email;

import codeChallenge.deepseek.architectureQualityAttributes.testability.nw.TestModeEnum;

public interface EmailSender {
    TestModeEnum type();

    void sendWelcome(String email);
}
