package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.resilience.second;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class LoggerServiceOld {

    private final Path logFile = Path.of("app.log");

    public void log(String userId, String message) {
        try {
            String logLine = String.format("%s: %s%n", userId, message);
            Files.writeString(logFile, logLine, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
