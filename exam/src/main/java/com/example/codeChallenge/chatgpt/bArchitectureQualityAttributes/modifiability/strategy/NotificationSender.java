package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.modifiability.strategy;

import codeChallenge.chatgpt.eCommon.Notification;
import codeChallenge.chatgpt.eCommon.NotificationType;

public interface NotificationSender {
    NotificationType getType();

    void connect();

    void send(Notification notification);

    void disconnect();
}
