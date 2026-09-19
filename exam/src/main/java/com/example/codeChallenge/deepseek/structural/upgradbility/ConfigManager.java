package com.example.codeChallenge.deepseek.structural.upgradbility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final String VERSION_2 = "#Version2";

    public void save(String filePath, Map<String, String> config) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filePath))) {
            writer.write(VERSION_2);
            writer.newLine();

            for (Map.Entry<String, String> entry : config.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        }
    }

    public Map<String, String> load(String filePath) throws IOException {
        Map<String, String> config = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            String line = reader.readLine();

            if (line == null) {
                return config;
            }

            boolean isVersion2 = line.startsWith(VERSION_2);

            if (!line.isEmpty() && isVersion2) {
                line = reader.readLine();//برو به خط 2
                loadV2(line, config, reader);
            } else {
                loadV1(line, config, reader);
            }

        }
        return config;
    }

    private static void loadV1(String line, Map<String, String> config, BufferedReader reader) throws IOException {
        while (line != null) {
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                config.put(parts[0], parts[1]);
            }

            line = reader.readLine();
        }
    }

    private static void loadV2(String line, Map<String, String> config, BufferedReader reader) throws IOException {
        while (line != null) {
            if (line.startsWith("#")) {
                line = reader.readLine();
                continue;
            }

            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                config.put(parts[0], parts[1]);
            }

            line = reader.readLine();
        }
    }
}