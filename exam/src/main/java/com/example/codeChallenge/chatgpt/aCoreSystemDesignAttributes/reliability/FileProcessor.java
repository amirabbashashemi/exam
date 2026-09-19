package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.reliability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileProcessor {
    private static final Map<String, Long> RESULT_MAP = new ConcurrentHashMap<>();

    public Map<String, Long> processFiles(List<Path> paths) {
        RESULT_MAP.clear();
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            for (Path path : paths) {
                fillMap(executorService, path);
            }

            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("An exception occurred in method processFiles", e);
        }

        return RESULT_MAP;
    }


    private void fillMap(ExecutorService executorService, Path path) {
        executorService.submit(() -> {
            try {
                String content = readFile(path);
                long wordCount = Arrays.stream(content.split(" ")).count();
                RESULT_MAP.put(path.toString(), wordCount);
            } catch (Exception ignored) {
                RESULT_MAP.put(path.toString(), -1L);
            }
        });

    }

    private String readFile(Path path) {
        String content = null;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            String errorMessage = String.format("An exception occurred in reading file %s", path.getFileName());
            throw new RuntimeException(errorMessage, e);
        }
        return content;
    }
}