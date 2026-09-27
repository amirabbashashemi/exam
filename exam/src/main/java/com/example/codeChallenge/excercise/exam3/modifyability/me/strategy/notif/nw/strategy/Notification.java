package com.example.codeChallenge.excercise.exam3.modifyability.me.strategy.notif.nw.strategy;

public interface Notification {
    String getNotificationType();

    void send(String userId, String message);
}
