package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.modifiability;

import codeChallenge.chatgpt.eCommon.Notification;
import codeChallenge.chatgpt.eCommon.NotificationType;

public class NotificationService {

    public void send(Notification notification) {

        if (notification.getType() == NotificationType.EMAIL) {
            connectToSmtpServer();
            sendEmail(notification);
            disconnect();
        }

        if (notification.getType() == NotificationType.SMS) {
            connectToSmsGateway();
            sendSms(notification);
            disconnect();
        }

        if (notification.getType() == NotificationType.PUSH) {
            connectToPushServer();
            sendPush(notification);
            disconnect();
        }
    }

    private void sendPush(Notification notification) {

    }

    private void connectToPushServer() {

    }

    private void sendSms(Notification notification) {

    }

    private void connectToSmsGateway() {

    }

    private void disconnect() {

    }

    private void sendEmail(Notification notification) {

    }

    private void connectToSmtpServer() {

    }
}
