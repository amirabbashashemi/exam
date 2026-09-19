package com.example.codeChallenge.deepseek.core.scalability;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class DataProcessor {
    private static final String INPUT_DIR = "./input/";
    private static final String OUTPUT_DIR = "./output/";
    private static final int batchSize = 1000;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();

    public void processAllFiles() throws IOException {
        File[] files = new File(INPUT_DIR).listFiles((d, name) -> name.endsWith(".csv"));

        if (!Objects.isNull(files)) {
            for (File file : files) {
                System.out.printf("Processing: %s%n", file.getName());
                EXECUTOR_SERVICE.submit(() -> processFile(file));
            }
        }

        try {
            // Graceful Shutdown
            EXECUTOR_SERVICE.shutdown();//todo important priority
            if (!EXECUTOR_SERVICE.awaitTermination(5, TimeUnit.MINUTES)) {
                EXECUTOR_SERVICE.shutdownNow();
            }
        } catch (InterruptedException interruptedException){
            Thread.currentThread().interrupt();
            throw new RuntimeException(interruptedException);
        }
    }

    private void processFile(File file) {
        Path path = Path.of(INPUT_DIR + file.getName());

        try (BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            List<CompletableFuture<String>> completableFutures = new ArrayList<>();

            List<String> transformLines = new ArrayList<>();

            bufferedReader.lines().forEach(line -> {
                CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> transformLine(line), EXECUTOR_SERVICE);//todo important
                completableFutures.add(completableFuture);
            });

            // منتظر اتمام همه
            CompletableFuture
                    .allOf(completableFutures.toArray(new CompletableFuture[0]))
                    .join();

            // گرفتن نتایج
            for (CompletableFuture<String> completableFuture : completableFutures) {
                String result = completableFuture.get();
                transformLines.add(result+"\n");

                if (transformLines.size() >= batchSize) {
                    String outputName = file.getName().replace(".csv", ".out");
                    try {
                        Files.write(
                                Paths.get(OUTPUT_DIR, outputName),
                                transformLines,
                                StandardOpenOption.APPEND);

                        transformLines.clear();
                    } catch (IOException e) {
                        System.out.printf("An exception occurred in processing of %s", outputName);
                        throw new RuntimeException(e);
                    }
                }
            }

        } catch (IOException ioException) {
            System.out.println("An exception occurred in method processFile");
            throw new RuntimeException(ioException);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private String transformLine(String line) {
        // پردازش سنگین: پارس کردن، تبدیل و اعتبارسنجی
        try {
            Thread.sleep(10); // شبیه‌سازی پردازش سنگین
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return line.toUpperCase() + "_TRANSFORMED";
    }
}

