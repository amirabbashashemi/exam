package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.data.residency.region.based.compliance.neww;

import java.util.ArrayList;
import java.util.List;

public class IRRegionSaver implements RegionSaver {
    private static final List<UserActivity> USER_ACTIVITIES = new ArrayList<>();

    @Override
    public Region type() {
        return Region.IR;
    }

    @Override
    public void save(UserActivity userActivity) {
        USER_ACTIVITIES.add(userActivity);
    }
}
