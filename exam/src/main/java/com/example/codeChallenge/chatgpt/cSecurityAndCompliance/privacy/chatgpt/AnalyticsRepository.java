package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.privacy.chatgpt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AnalyticsRepository {

    private static final long RETENTION_DAYS = 30;
    private final List<AnalyticsEvent> events = new ArrayList<>();

    public void save(AnalyticsEvent event) {
        events.add(event);
    }

    public List<AnalyticsEvent> findAll() {
        applyRetentionPolicy();
        return events;
    }

    private void applyRetentionPolicy() {
        long cutoff = Instant.now()
                .minus(RETENTION_DAYS, ChronoUnit.DAYS)
                .toEpochMilli();

        Iterator<AnalyticsEvent> iterator = events.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getTimestamp() < cutoff) {
                iterator.remove();
            }
        }
    }
}