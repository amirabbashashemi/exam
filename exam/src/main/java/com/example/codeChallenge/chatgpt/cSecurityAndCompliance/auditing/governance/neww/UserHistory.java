package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.auditing.governance.neww;

public class UserHistory {
    private Integer version;
    private Long id;
    private String name;
    private String email;
    private Long changerUser;
    private long timestamp;


    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getChangerUser() {
        return changerUser;
    }

    public void setChangerUser(Long changerUser) {
        this.changerUser = changerUser;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
