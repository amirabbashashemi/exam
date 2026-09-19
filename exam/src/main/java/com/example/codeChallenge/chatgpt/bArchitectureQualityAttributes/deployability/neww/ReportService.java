package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.deployability.neww;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class ReportService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ReportService.class);

    private final ReportConfiguration reportConfiguration;

    public ReportService(ReportConfiguration reportConfiguration) {
        this.reportConfiguration = reportConfiguration;
    }

    public void saveReport(String name, String content) {
        try (FileWriter writer = new FileWriter(Paths.get(this.reportConfiguration.getBasePath(), name).toFile())) {
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException("Cannot save report", e);
        }
    }

}

