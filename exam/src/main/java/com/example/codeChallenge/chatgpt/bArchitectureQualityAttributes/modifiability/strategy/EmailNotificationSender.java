package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.modifiability.strategy;

import codeChallenge.chatgpt.eCommon.Notification;
import codeChallenge.chatgpt.eCommon.NotificationType;

public class EmailNotificationSender implements NotificationSender {
    @Override
    public NotificationType getType() {
        return NotificationType.EMAIL;
    }

    @Override
    public void connect() {

    }

    @Override
    public void send(Notification notification) {
        try {
            // send email
        } catch (Exception exception) {
            throw new RuntimeException("Error in method send in class EmailNotificationSender", exception);
        }
    }

    @Override
    public void disconnect() {

    }
}
