package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.data.residency.region.based.compliance.neww;

import java.util.Map;
import java.util.Objects;

public class RegionSaverContext {
    private final Map<Region, RegionSaver> regionRegionSaverMap;

    public RegionSaverContext(Map<Region, RegionSaver> regionRegionSaverMap) {
        this.regionRegionSaverMap = regionRegionSaverMap;
    }

    public void save(UserActivity userActivity) {
        RegionSaver regionSaver = regionRegionSaverMap.get(userActivity.getRegion());

        if (Objects.isNull(regionSaver)) {
            throw new RuntimeException(regionSaver + " is not valid");
        } else {
            regionSaver.save(userActivity);
        }
    }

}
