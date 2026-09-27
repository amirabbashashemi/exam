package com.example.codeChallenge.excercise.exam3.modifyability.me.strategy.notif.old;

public class NotificationService {
    public void send(String userId, String message) {
        // سخت‌کد شده به Email
        EmailClient emailClient = new EmailClient();

        emailClient.connect();
        emailClient.send(userId, message);
        emailClient.disconnect();
    }
}