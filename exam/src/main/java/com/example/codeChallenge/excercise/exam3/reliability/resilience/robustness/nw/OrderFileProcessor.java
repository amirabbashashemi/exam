package com.example.codeChallenge.excercise.exam3.reliability.resilience.robustness.nw;

//TODO *****

/*
یک سرویس Order File Processor داریم که فایل‌های سفارش را از یک دایرکتوری ورودی می‌خواند، پردازش می‌کند و در دایرکتوری خروجی می‌نویسد.

مشکلات فعلی:
اگر در حین پردازش خطایی رخ دهد، برنامه کرش می‌کند.
اگر برنامه وسط کار بسته شود، هیچ نشانی از فایل‌های پردازش‌شده ندارد و دوباره همه را از اول پردازش می‌کند.
هیچ اعتبارسنجی روی محتوای فایل انجام نمی‌شود.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class OrderFileProcessor {
    private final Path inputDir;
    private final Path outputDir;
    private final Path dlqPointPath;
    private final Path checkPointPath;
    private final Map<String, Boolean> processedMap = new ConcurrentHashMap<>();

    public OrderFileProcessor(Path inputDir, Path outputDir, Path dlqPointPath, Path checkPointPath) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.dlqPointPath = dlqPointPath;
        this.checkPointPath = checkPointPath;

        recover();
    }

    private void recover() {
        if (Files.exists(checkPointPath)) {
            addToMap(checkPointPath);
        }
    }

    private void addToMap(Path path) {
        String fileName = getName(path);

        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            String line = bufferedReader.readLine();

            while (line != null) {

                processedMap.put(line, Boolean.TRUE);//TODO -> line باید اضافه شود

                line = bufferedReader.readLine();
            }
        } catch (Exception e) {
            System.err.printf("Error in method addToMap for file %s", fileName);
        }
    }

    public void processAll() throws IOException {
        try (Stream<Path> files = Files.list(inputDir)) {
            for (Path path : files.toList()) {
                String fileName = getName(path);
                try {
                    Boolean aBoolean = processedMap.get(fileName);

                    if (aBoolean) {
                        continue;
                    }

                    process(path);
                } catch (Exception e) {
                    System.err.printf("Error in method processAll for file: %s", fileName);
                }
            }
        } catch (Exception e) {
            System.err.printf("Error in method processAll errorMessage: %s", e.getMessage());
        }
    }

    private void process(Path path) {
        String fileName = getName(path);

        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {

            String line = bufferedReader.readLine();

            while (line != null) {
                if (validateLine(line)) {//TODO -> ولیدیشن
                    line = bufferedReader.readLine();
                    continue;
                }

                // پردازش ساده: تبدیل به حروف بزرگ
                String processed = line.toUpperCase();

                Path resolve = outputDir.resolve(path.getFileName());

                Files.writeString(resolve,
                        processed,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);


                line = bufferedReader.readLine();
            }

            saveFileName(fileName);//TODO -> fdv,k hc pgri

            processedMap.put(fileName, Boolean.TRUE);
        } catch (Exception e) {
            System.err.printf("Error in method process for file %s . errorMessage:%s", fileName, e.getMessage());
            try {
                Path resolve = outputDir.resolve(path.getFileName());
                if (Files.exists(resolve)) {//TODO -> برای اتمیک بودن
                    Files.delete(resolve);
                }
                Files.writeString(dlqPointPath,//TODO -> DLQ
                        fileName + "\n",
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
            } catch (Exception exception) {
                System.err.printf("Error in save dead letter queue for file %s . errorMessage:%s", fileName, e.getMessage());
            }
        }
    }

    private boolean validateLine(String line) {
        return line == null || line.isEmpty();
    }

    private void saveFileName(String fileName) {
        try {
            String line = fileName + "\n";

            Files.writeString(
                    checkPointPath,
                    line,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (Exception e) {
            System.err.printf("Error in method saveFileName for file %s . errorMessage:%s", fileName, e.getMessage());
        }
    }

    private String getName(Path path) {
        return path.toFile().getName();
    }


}