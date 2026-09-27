package com.example.codeChallenge.excercise.exam3.modifyability.me.strategy.notif.nw.strategy;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class NotificationService {
    private String configURL;
    private Properties properties = new Properties();
    private List<Notification> notifications = new ArrayList<>();

    //for inject
    public NotificationService() {
        setConfigURL();
        loadConfigs();
        ServiceLoader<Notification> serviceLoader = ServiceLoader.load(Notification.class);

        for (Notification notification : serviceLoader) {
            notifications.add(notification);
        }
    }

    //for testability
    public NotificationService(List<Notification> notifications, String notificationType) {
        setConfigURL();
        properties.setProperty("default-notification-type", notificationType);
        this.notifications = notifications;
    }

    public void send(String notificationType, String userId, String message) {
        Notification notification = getNotification(notificationType);
        notification.send(userId, message);
    }

    private Notification getNotification(String notificationType) {
        Optional<Notification> notificationOptional = notifications.stream()
                .filter(notification -> notification.getNotificationType().equals(notificationType))
                .findFirst();

        if (notificationOptional.isEmpty()) {
            String defaultNotificationType = properties.get("default-notification-type").toString();
            notificationOptional = notifications.stream()
                    .filter(notification -> notification.getNotificationType().equalsIgnoreCase(defaultNotificationType))
                    .findFirst();
        }

        if (notificationOptional.isEmpty()) {
            throw new RuntimeException(String.format("%s is invalid", notificationType));
        }

        return notificationOptional.get();
    }

    private void setConfigURL() {
        String envConfigURL = System.getenv("config-url");

        if (envConfigURL != null && !envConfigURL.isEmpty()) {
            configURL = envConfigURL;
        }

        if (configURL == null || configURL.isEmpty()) {
            configURL = System.getProperty("config-url");
        }

    }

    private void loadConfigs() {
        try (InputStream inputStream = new FileInputStream(configURL)) {
            properties.load(inputStream);
        } catch (Exception e) {
            fallBack();
        }
    }

    private void fallBack() {
        properties.setProperty("default-notification-type", "EMAIL");
    }
}
