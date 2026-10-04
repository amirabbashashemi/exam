package com.example.codeChallenge.deepseek.core.continuity;




import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public class PartialFileDownloadDS {
    private static final int BUFFER_SIZE = 8192;
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    public static void downloadWithResume(String fileUrl, Path outputPath) throws IOException, InterruptedException {
        long existingSize = Files.exists(outputPath) ? Files.size(outputPath) : 0;

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(fileUrl))
                .timeout(TIMEOUT)
                .header("User-Agent", "Java HttpClient");

        if (existingSize > 0) {
            builder.header("Range", "bytes=" + existingSize + "-");
        }

        HttpRequest request = builder.GET().build();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        int status = response.statusCode();
        boolean append;
        long startOffset;

        if (status == 206) {
            // ✅ سرور از Resume پشتیبانی می‌کند
            String contentRange = response
                    .headers()
                    .firstValue("Content-Range")
                    .orElseThrow(() -> new IOException("Missing Content-Range header for 206 response"));

            // فرمت: bytes start-end/total
            long serverStart = parseStartOffset(contentRange);
            if (serverStart != existingSize) {
                throw new IOException("Server returned unexpected start offset: " +
                        serverStart + " (expected " + existingSize + ")");
            }

            append = true;
            startOffset = existingSize;
            System.out.println("✅ Server supports resume (206). Appending from " + existingSize);
        } else if (status == 200) {
            // ⚠️ سرور Range را نادیده گرفته یا اصلاً Range نداشته‌ایم
            if (existingSize > 0) {
                System.out.println("⚠️ Server does not support resume (200). Restarting download from scratch.");
            } else {
                System.out.println("✅ Starting fresh download (200).");
            }
            append = false; // فایل را از اول می‌نویسیم (Truncate)
            startOffset = 0;
        } else if (status == 416) {
            // Range Not Satisfiable → احتمالاً فایل از قبل کامل دانلود شده
            System.out.println("ℹ️ Server returned 416. File may already be fully downloaded.");
            return;
        } else {
            throw new IOException("Unexpected HTTP status: " + status);
        }

        try (InputStream in = response.body();
             FileOutputStream fos = new FileOutputStream(outputPath.toFile(), append)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long totalRead = startOffset;

            while ((bytesRead = in.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
                if (totalRead % (5 * 1024 * 1024) < BUFFER_SIZE) {
                    System.out.println("⬇️ Downloaded: " + totalRead + " bytes");
                }
            }
            System.out.println("✅ Download completed: " + totalRead + " bytes");
        }
    }

    private static long parseStartOffset(String contentRange) throws IOException {
        // Content-Range: bytes 12345-67890/123456
        try {
            String range = contentRange.substring(contentRange.indexOf(' ') + 1);
            String start = range.substring(0, range.indexOf('-'));
            return Long.parseLong(start);
        } catch (Exception e) {
            throw new IOException("Invalid Content-Range: " + contentRange, e);
        }
    }
}

