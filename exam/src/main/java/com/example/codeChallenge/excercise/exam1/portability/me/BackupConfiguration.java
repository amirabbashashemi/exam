package com.example.codeChallenge.excercise.exam1.portability.me;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;



public class BackupConfiguration {
    private Path tempFilePath;
    private Path sourceFilePath;
    private Path targetFilePath;

    public BackupConfiguration(String configUri) {
        try (InputStream inputStream = getConfigOrDefault(configUri)) {
            initiate(inputStream);
        } catch (IOException runtimeException) {
            throw new RuntimeException("IOException exception occurred.", runtimeException);
        } catch (Exception exception) {
            throw new RuntimeException("An exception occurred in constructor.", exception);
        }
    }

    private InputStream getConfigOrDefault(String configUri) throws IOException {
        if (configUri == null || configUri.isEmpty()) {
            // اول از classpath امتحان کن
            InputStream is = getClass().getClassLoader().getResourceAsStream("default.application.properties");
            if (is != null) return is;
            // بعد از فایل‌سیستم
            String homeUri = System.getProperty("user.home");
            Path path = Path.of(homeUri, "config", "default.application.properties");
            return Files.newInputStream(path);
        }
        // اگر configUri داخل classpath بود
        InputStream is = getClass().getClassLoader().getResourceAsStream(configUri);
        if (is != null) return is;
        return Files.newInputStream(Path.of(configUri));
    }

    private void initiate(InputStream inputStream) {
        try {
            Properties properties = new Properties();

            properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String temp = getProperty(properties, "file.temp");
            String source = getProperty(properties, "file.source");
            String target = getProperty(properties, "file.target");

            tempFilePath = Path.of(temp);
            sourceFilePath = Path.of(source);
            targetFilePath = Path.of(target);
        } catch (IOException e) {
            throw new RuntimeException("exception occurred in loading configuration file ", e);
        }
    }

    private String getProperty(Properties properties, String name) {
        Object object = properties.get(name);
        return object != null ? object.toString() : "-";
    }

    public Path getTempFilePath() {
        return tempFilePath;
    }

    public Path getSourceFilePath() {
        return sourceFilePath;
    }

    public Path getTargetFilePath() {
        return targetFilePath;
    }

}
