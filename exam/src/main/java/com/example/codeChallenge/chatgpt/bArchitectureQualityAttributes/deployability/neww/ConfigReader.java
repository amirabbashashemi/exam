package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.deployability.neww;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigReader {
    private final String configUriFullPath;
    private final Map<String, String> configMap = new ConcurrentHashMap<>();

    public ConfigReader(String configUri) {
        this.configUriFullPath = configUri;
        initiateMap();
    }

    private void initiateMap() {
        try {
            URI uri = new URI(this.configUriFullPath);
            Path path = Paths.get(uri);
            if (Files.exists(path)) {
                List<String> strings = Files.readAllLines(path, StandardCharsets.UTF_8);

                for (String line : strings) {
                    if (line != null) {
                        String[] arrayString = line.trim().split("=");
                        if (arrayString.length == 2) {
                            configMap.put(arrayString[0], arrayString[1]);
                        }
                    }
                }
            } else {
                throw new RuntimeException(String.format("File %s is not exists", this.configUriFullPath));
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public String getConfigValueByKey(String key) {
        return configMap.get(key);
    }
}
