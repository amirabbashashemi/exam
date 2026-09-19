package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.deployability.neww;

public class ReportConfiguration {
    private final ConfigReader configReader;

    public ReportConfiguration(ConfigReader configReader) {
        this.configReader = configReader;
    }

    public String getBasePath() {
        return configReader.getConfigValueByKey("base-path");
    }
}
