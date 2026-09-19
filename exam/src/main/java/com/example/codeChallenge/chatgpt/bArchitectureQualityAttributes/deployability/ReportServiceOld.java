package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.deployability;

import java.io.FileWriter;
import java.io.IOException;

public class ReportServiceOld {
    private final String basePath;

    public ReportServiceOld() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            basePath = "C:\\reports\\";
        } else {
            basePath = "/var/reports/";
        }
    }

    public void saveReport(String name, String content) {
        try {
            FileWriter writer = new FileWriter(basePath + name);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Cannot save report", e);
        }
    }
}
