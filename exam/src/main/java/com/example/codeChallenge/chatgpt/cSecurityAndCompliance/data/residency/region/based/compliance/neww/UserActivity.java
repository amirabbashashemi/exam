package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.data.residency.region.based.compliance.neww;

public class UserActivity {
    private Long userId;
    private Region region; // EU, IR
    private String action;
    private long timestamp;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}