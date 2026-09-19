package com.example.codeChallenge.excercise.exam2.thread.safety;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public class FileProcessor {

    private final AtomicInteger processedCount = new AtomicInteger();

    public void process(Path path) {

        // پردازش فایل ...

        processedCount.getAndIncrement();
    }

    public int getProcessedCount() {
        return processedCount.get();
    }

}
