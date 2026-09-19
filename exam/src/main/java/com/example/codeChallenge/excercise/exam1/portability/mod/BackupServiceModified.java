package com.example.codeChallenge.excercise.exam1.portability.mod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class BackupServiceModified {
    private String source;
    private String target;

    public BackupServiceModified() {
        initiate();
    }

    private void initiate() {
        try {
            Path path = Path.of("application.properties");
            List<String> strings = Files.readAllLines(path);

            strings.forEach(line -> {
                String[] split = line.split("=");
                if ("file.source".equals(split[0])) {
                    source = split[1].trim();
                }
                if ("file.target".equals(split[0])) {
                    target = split[1].trim();
                }
            });
        } catch (IOException ioException) {
            throw new RuntimeException("An exception occurred in initiating configs.", ioException);
        }
    }

    public void backup() throws Exception {
        Files.copy(
                Paths.get(source),
                Paths.get(target),
                StandardCopyOption.REPLACE_EXISTING
        );

    }

}

