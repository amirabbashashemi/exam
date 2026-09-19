package com.example.codeChallenge.deepseek.structural.configurability;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class AlertConfiguration {
    private static final Properties PROPERTIES = new Properties();
    private static final String DEFAULT_LOG_LEVEL = "INFO";
    private static final String DEFAULT_ENABLE_EMAIL_ALERTS = "true";
    private static final String DEFAULT_MAX_RETRY_COUNT = "3";
    private static final String DEFAULT_RETRY_DELAY_MS = "1000";
    private static final String DEFAULT_ALERT_RECIPIENT = "admin@company.com";


    public AlertConfiguration() {
        String configUrl = getConfigUrl();

        try {
            Path path = Path.of(configUrl);
            boolean configFileExists = Files.exists(path);

            if (configFileExists) {
                FileInputStream fileInputStream = new FileInputStream(path.toFile());

                PROPERTIES.load(fileInputStream);
            } else {
                System.err.printf("Config file is not available ad url: %s", configUrl);
            }
        } catch (IOException e) {
            System.err.println("Error in reading config file");
        }
    }

    private String getConfigUrl() {
        String configUrl = System.getProperty("config.file");

        if (configUrl == null) {
            configUrl = System.getenv("config-url");
        }

        if (configUrl == null) {
            configUrl = "application.properties";
        }

        return configUrl;
    }

    public String getLogLevel() {
        return PROPERTIES.getProperty("LOG_LEVEL", DEFAULT_LOG_LEVEL);
    }

    public boolean isEnableEmailAlerts() {
        String enableEmailAlerts = PROPERTIES.getProperty("ENABLE_EMAIL_ALERTS", DEFAULT_ENABLE_EMAIL_ALERTS);

        try {
            return "true".equalsIgnoreCase(enableEmailAlerts);
        } catch (Exception e) {
            System.err.printf("Format exception on config: %s", enableEmailAlerts);
            return "true".equalsIgnoreCase(DEFAULT_ENABLE_EMAIL_ALERTS);
        }

    }

    public int getMaxRetryCount() {
        return getInt("MAX_RETRY_COUNT", DEFAULT_MAX_RETRY_COUNT);
    }

    public int getRetryDelayMS() {
        return getInt("RETRY_DELAY_MS", DEFAULT_RETRY_DELAY_MS);
    }

    private int getInt(String value, String defaultValue) {
        String property = PROPERTIES.getProperty(value, defaultValue);

        try {
            return Integer.valueOf(property);
        } catch (Exception e) {
            System.err.printf("Format exception on config: %s", value);
            return Integer.valueOf(defaultValue);
        }

    }

    public String getAlertRecipient() {
        return PROPERTIES.getProperty("ALERT_RECIPIENT", DEFAULT_ALERT_RECIPIENT);
    }

}
