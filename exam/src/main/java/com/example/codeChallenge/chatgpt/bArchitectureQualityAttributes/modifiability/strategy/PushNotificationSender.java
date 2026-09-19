package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.modifiability.strategy;

import codeChallenge.chatgpt.eCommon.Notification;
import codeChallenge.chatgpt.eCommon.NotificationType;

public class PushNotificationSender implements NotificationSender {
    @Override
    public NotificationType getType() {
        return NotificationType.PUSH;
    }

    @Override
    public void connect() {

    }

    @Override
    public void send(Notification notification) {
        try {
            // push notification
        } catch (Exception exception) {
            throw new RuntimeException("Error in method send in class PushNotificationSender", exception);
        }
    }

    @Override
    public void disconnect() {

    }
}
