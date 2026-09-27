package com.example.codeChallenge.excercise.exam3.modifyability.me.strategy.notif.nw.strategy.impl;

import com.example.codeChallenge.excercise.exam3.modifyability.me.strategy.notif.nw.strategy.Notification;

public class SMSNotification implements Notification {

    @Override
    public String getNotificationType() {
        return "SMS";
    }

    @Override
    public void send(String userId, String message) {
        //simulation of Email SMSNotification
    }
}
