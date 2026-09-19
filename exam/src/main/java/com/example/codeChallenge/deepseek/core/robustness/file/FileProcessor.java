package com.example.codeChallenge.deepseek.core.robustness.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FileProcessor {
    private final String inputDir = "./input/";
    private final String outputDir = "./output/";
    private final Map<String, List<String>> FILENAME_ERROR_MESSAGE_MAP = new ConcurrentHashMap<>();
    private final List<String> processedFiles = new ArrayList<>();

    public void processAllFiles() throws IOException {
        File[] files = new File(inputDir).listFiles((d, name) -> name.endsWith(".csv"));

        if (Objects.isNull(files) || files.length == 0) {
            System.out.println("No CSV files found in " + inputDir);
            return;
        }

        for (File file : files) {
            processFile(file);
        }

        for (File file : files) {
            printErrors(file.getName());
        }
    }

    private void processFile(File file) throws IOException {
        try {
            System.out.println("Processing: " + file.getName());

            //ارزیابی فالی
            boolean valid = validateFile(file);
            if (!valid) {
                logException(file.getName(), "File is not valid");
                return;
            }

            // خواندن فایل
            List<String> lines = readFileLines(file);

            //پردازش فایل
            List<String> results = processFile(file, lines);

            // ذخیره خروجی
            writeInFile(file, results);

            processedFiles.add(file.getName());
        } catch (Exception e) {
            logException(file.getName(), "Critical error: " + e.getMessage());
            System.err.println("Error processing " + file.getName());
        }
    }

    private boolean validateFile(File file) {
        return Objects.nonNull(file) && file.exists();
    }

    private List<String> readFileLines(File file) {
        List<String> strings = new ArrayList<>();

        try {
            strings = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            String errorMessage = "Error in loading the file: " + file.getName();
            System.err.println(errorMessage);
            logException(file.getName(), errorMessage);
        }

        return strings;
    }

    private List<String> processFile(File file, List<String> lines) {
        List<String> results = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(",");

            if (parts.length != 3) {
                String errorMessage = "line is incorrect: " + line;
                System.err.println(errorMessage);
                logException(file.getName(), errorMessage);

                continue;
            } else {
                String name = parts[0];
                int age = getAge(parts[1]);
                double salary = getSalary(parts[2]);

                String result = String.format("%s,%d,%.2f", name.toUpperCase(), age + 5, salary * 1.1);
                results.add(result);
            }
        }

        return results;
    }

    private Integer getAge(String age) {
        try {
            return Integer.parseInt(age);
        } catch (RuntimeException runtimeException) {
            System.err.printf("Type miss match exception for age : %s", age);
            return -1;
        }
    }

    private double getSalary(String salary) {
        try {
            return Double.parseDouble(salary);
        } catch (RuntimeException runtimeException) {
            System.err.printf("Type miss match exception for salary : %s", salary);
            return -1;
        }
    }

    private void writeInFile(File file, List<String> results) {
        boolean valid = validateFile(file);
        if (!valid) {
            logException(file.getName(), "File is not valid");
            return;
        }

        String outputName = "";
        try {
            outputName = (file.getName() + System.currentTimeMillis()).replace(".csv", ".out");

            Files.write(Paths.get(outputDir, outputName), results);
        } catch (IOException e) {
            String errorMessage = "Error in writing into the file: %s%n" + outputName;
            System.err.printf(errorMessage);
            logException(file.getName(), errorMessage);
        }
    }

    private void logException(String fileName, String message) {
        List<String> strings = FILENAME_ERROR_MESSAGE_MAP.get(fileName);

        if (Objects.isNull(strings)) {
            strings = new ArrayList<>();
            strings.add(message);
            FILENAME_ERROR_MESSAGE_MAP.put(fileName, strings);
        } else {
            strings.add(message);
        }

    }

    private void printErrors(String fileName) {
        List<String> errors = FILENAME_ERROR_MESSAGE_MAP.get(fileName);
        if (Objects.isNull(errors) || errors.isEmpty()) {
            System.out.println("There is not any error for file " + fileName);
        } else {
            System.out.println("There is/are errors for file " + fileName);
            errors.forEach(System.out::println);
        }
    }

}