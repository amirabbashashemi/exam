package com.example.codeChallenge.deepseek.core.scalability;





import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class DataProcessor {
    private final Path inputDir;
    private final Path outputDir;
    private final int batchSize;
    private final ExecutorService executor;

    public DataProcessor(Path inputDir, Path outputDir, int batchSize, ExecutorService executor) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.batchSize = batchSize;
        this.executor = executor;
    }

    public void processAllFiles() {
        List<Path> files;
        try (var stream = Files.list(inputDir)) {
            files = stream
                    .filter(p -> p.toString().endsWith(".csv"))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Cannot list input directory: " + inputDir, e);
        }

        List<CompletableFuture<Void>> futures = files
                .stream()
                .map(file -> CompletableFuture
                        .runAsync(() -> processFile(file), executor)
                        .exceptionally(ex -> {
                            System.err.println("Failed to process " + file + ": " + ex.getMessage());
                            return null; // یک فایل خراب، بقیه را متوقف نمی‌کند
                        }))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void processFile(Path file) {
        Path outputFile = outputDir.resolve(
                file.getFileName().toString().replace(".csv", ".out")
        );

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            List<String> batch = new ArrayList<>(batchSize);
            String line;

            while ((line = reader.readLine()) != null) {
                batch.add(transformLine(line));

                if (batch.size() >= batchSize) {
                    writeBatch(outputFile, batch);
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                writeBatch(outputFile, batch);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing file: " + file, e);
        }
    }

    private void writeBatch(Path outputFile, List<String> batch) throws IOException {
        Files.write(
                outputFile,
                batch,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    private String transformLine(String line) {
        try {
            Thread.sleep(10); // شبیه‌سازی پردازش سنگین
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return line.toUpperCase() + "_TRANSFORMED";
    }
}