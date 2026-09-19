package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.data.residency.region.based.compliance.old;

import codeChallenge.chatgpt.cSecurityAndCompliance.data.residency.region.based.compliance.neww.Region;
import codeChallenge.chatgpt.cSecurityAndCompliance.data.residency.region.based.compliance.neww.UserActivity;

import java.util.*;

public class ActivityService {
    private final Map<Region, List<UserActivity>> regionUserActivityMap = new HashMap<>();

    public void save(UserActivity activity) {
        if (activity.getRegion() == null) {
            throw new IllegalArgumentException("Region is required");
        }

        List<UserActivity> userActivities = regionUserActivityMap.get(activity.getRegion());

        if (Objects.isNull(userActivities)) {
            userActivities = new ArrayList<>();
        }
        userActivities.add(activity);

        regionUserActivityMap.put(activity.getRegion(), userActivities);
    }

    public List<UserActivity> findAll(Region region) {
        return regionUserActivityMap.get(region);
    }
}

/*
Problems (که باید حل کنی)
Data Residency رعایت نشده
Region فقط یک String است و هیچ کنترلی ندارد
ذخیره‌سازی Region-aware نیست
اگر فردا US اضافه شود، کد به‌هم می‌ریزد
 */