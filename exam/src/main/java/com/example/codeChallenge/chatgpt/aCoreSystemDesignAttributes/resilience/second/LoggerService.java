package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.resilience.second;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.*;

public class LoggerService {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();
    private static final BlockingQueue<String> LOG_BLOCKING_QUEUE = new LinkedBlockingQueue<>();

    private final Path logFile = Path.of("app.log");

    public LoggerService() {
        subscribe();
    }

    public void log(String message) {
        try {
            publish(message);
        } catch (Exception exception) {
            throw new RuntimeException("Ac exception occurred in method log", exception);
        }
    }

    private void publish(String logString) {
        try {
            LOG_BLOCKING_QUEUE.add(logString);
        } catch (Exception exception) {
            throw new RuntimeException("Ac exception occurred in method publish", exception);
        }
    }

    private void subscribe() {
        try {
            EXECUTOR_SERVICE.submit(() -> {
                while (true) {
                    String logString = LOG_BLOCKING_QUEUE.take();
                    Files.writeString(logFile, logString, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
            });
        } catch (Exception exception) {
            throw new RuntimeException("Ac exception occurred in method subscribe", exception);
        }
    }

    private void doShutdownHook() {
        Thread thread = new Thread(() -> {
            shutDownExecutor();
            backupLogs();
        });

        Runtime.getRuntime().addShutdownHook(thread);
    }

    private void shutDownExecutor() {
        if (!EXECUTOR_SERVICE.isShutdown()) {
            EXECUTOR_SERVICE.shutdown();
        }
    }

    private void backupLogs() {
        try {
            Path path = Path.of(new URI("backup-logs.log"));
            String logString = LOG_BLOCKING_QUEUE.poll(5, TimeUnit.SECONDS);

            if (logString != null) {
                while (logString != null) {
                    Files.writeString(path, logString);
                    logString = LOG_BLOCKING_QUEUE.poll(5, TimeUnit.SECONDS);
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException("Ac exception occurred in method subscribe", exception);
        }
    }
    /*
    🔹 مشکلات فعلی:
وقتی تعداد logها زیاد شود، متد کند می‌شود → blocking I/O
اگر فایل system crash شود، logها از بین می‌روند → no continuity / disaster recovery
چند thread همزمان log می‌زنند → ممکن است logها خراب یا overwritten شود
هیچ mechanism برای batching یا async وجود ندارد → performance پایین
هیچ observability / monitoring برای log process وجود ندارد
     */

}
