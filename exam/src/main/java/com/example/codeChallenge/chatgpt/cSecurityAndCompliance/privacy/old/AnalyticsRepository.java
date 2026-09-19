package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.privacy.old;

import codeChallenge.chatgpt.eCommon.UserActivity;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsRepository {

    private final List<UserActivity> activities = new ArrayList<>();

    public void save(UserActivity activity) {
        activities.add(activity);
    }

    public List<UserActivity> findAll() {
        return activities;
    }
}
