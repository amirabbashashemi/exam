package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.portability.neww;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {
    private final FileConfiguration fileConfiguration;

    public FileManager(FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
    }

    public void saveText(String filename, String content) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                Path.of(fileConfiguration.getUri(), filename), StandardCharsets.UTF_8)) {
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException("Cannot save file", e);
        }
    }

    public String readText(String filename) {
        try (BufferedReader reader =
                     Files.newBufferedReader(Path.of(fileConfiguration.getUri(), filename), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file", e);
        }
    }
}
/*
StandardCharsets.UTF_8 *****************************

private final FileConfiguration fileConfiguration;

    public FileManager(FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
    }
***********************************

استفاده از Path
Path path = Path.of(fileConfiguration.getUri(), filename);


try(FileWriter writer = new FileWriter(Path.of(fileConfiguration.getUri(), filename).toFile(), StandardCharsets.UTF_8);
try(BufferedReader reader = Files.newBufferedReader(Path.of(fileConfiguration.getUri(), filename), StandardCharsets.UTF_8))
 */