package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.modifiability.strategy;

import codeChallenge.chatgpt.eCommon.Notification;
import codeChallenge.chatgpt.eCommon.NotificationType;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationSenderContext {
    private final Map<NotificationType, NotificationSender> typeFinalPriceCalculatorMap = new ConcurrentHashMap<>();

    public void registerNotificationSender(NotificationSender notificationSender) {
        typeFinalPriceCalculatorMap.put(notificationSender.getType(), notificationSender);
    }

    public void send(Notification notification) {
        NotificationSender notificationSender = null;
        try {
            notificationSender = getFinalPriceCalculator(notification.getType());
            notificationSender.connect();
            notificationSender.send(notification);
        } catch (Exception exception) {
            throw new RuntimeException(STR."Error in method send. for notification \{notification.toString()}. ", exception);
        } finally {
            if (Objects.nonNull(notificationSender)) {
                notificationSender.disconnect();
            }
        }
    }

    private NotificationSender getFinalPriceCalculator(NotificationType notificationType) {
        NotificationSender notificationSender = typeFinalPriceCalculatorMap.get(notificationType);
        if (Objects.nonNull(notificationSender)) {
            return notificationSender;
        } else {
            throw new RuntimeException(STR."Error in method getFinalPriceCalculator. \{notificationType} is ot valid ");
        }
    }

}
