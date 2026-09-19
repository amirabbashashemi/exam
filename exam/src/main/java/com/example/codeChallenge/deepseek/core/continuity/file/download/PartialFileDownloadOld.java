package com.example.codeChallenge.deepseek.core.continuity.file.download;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PartialFileDownloadOld {
    private static final int BUFFER_SIZE = 8192;
    private static final String fileUrl = "https://example.com/largeFile.zip";
    private final Path outputPath = Paths.get("largeFile.zip");

    public void downloadWithResume() throws IOException, InterruptedException {
        long downloaded = 0;
        if (Files.exists(outputPath)) {
            downloaded = Files.size(outputPath);
            System.out.println("📁 Found existing file: " + downloaded + " bytes downloaded.");
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(fileUrl))
                .header("User-Agent", "Java HttpClient");

        if (downloaded > 0) {
            builder.header("Range", "bytes=" + downloaded + "-");
        }

        HttpRequest request = builder.GET().build();
        HttpResponse<InputStream> response=null;// = client.send(request, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();
        if (status == 200 || status == 206) {
            System.out.println("✅ Server responded with " + status);

            try (InputStream in = response.body();
                 FileOutputStream fos = new FileOutputStream(outputPath.toFile(), true)) { // ← true = append

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                long totalRead = downloaded;

                while ((bytesRead = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                    if (totalRead % (5 * 1024 * 1024) < BUFFER_SIZE) {
                        System.out.println("⬇️ Downloaded: " + totalRead + " bytes");
                    }
                }
                System.out.println("✅ Download completed: " + totalRead + " bytes");
            }
        } else {
            throw new IOException("Unexpected HTTP status: " + status);
        }
    }

}