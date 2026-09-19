package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.continuity.download;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PartialFileDownload {
    private static final long CHUNK_SIZE = 5 * 1024 * 1024; // 5MB

    private void resumeDownload() throws IOException, InterruptedException {
        String url = "https://example.com/largeFile.zip";
        Path output = Paths.get("largeFile.zip");

        long downloadedBytes = 0;
        if (Files.exists(output)) {
            downloadedBytes = Files.size(output);
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();

        // اگر بخشی از فایل دانلود شده، ادامه بده
        if (downloadedBytes > 0) {
            requestBuilder.header("Range", "bytes=" + downloadedBytes + "-");
        }

        HttpRequest httpRequest = requestBuilder.build();
        HttpResponse<InputStream> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());

        // اگر سرور resume را قبول کرده باشد، کد 206 برمی‌گرداند
        if (downloadedBytes > 0) {
            if (httpResponse.statusCode() == 206) {// همه چیز درست است، ادامه دانلود
                try (InputStream in = httpResponse.body();
                     OutputStream out = new BufferedOutputStream(
                             new FileOutputStream(output.toFile(), downloadedBytes > 0))
                ) {
                    byte[] buffer = new byte[8192];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                }
            } else if (httpResponse.statusCode() == 200) {
                throw new RuntimeException(". سرور resume را پشتیبانی نمی‌کند، دانلود از ابتدا");
            } else {
                throw new RuntimeException("خطای دانلود: HTTP " + httpResponse.statusCode());
            }
        } else {
            if (httpResponse.statusCode() != 200) {
                throw new RuntimeException("خطای دانلود: HTTP " + httpResponse.statusCode());
            }
        }
    }


    private void resumeUpload(Path file, long offset, String uploadUrl) throws IOException {
        long fileSize = Files.size(file);

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file.toFile(), "r")) {
            randomAccessFile.seek(offset);

            byte[] buffer = new byte[(int) Math.min(CHUNK_SIZE, fileSize - offset)];
            int bytesRead = randomAccessFile.read(buffer);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .header("Content-Type", "application/octet-stream")
                .header("Content-Range", "bytes " + offset + "-" + (offset + bytesRead - 1) + "/" + fileSize)
                .PUT(HttpRequest.BodyPublishers.ofByteArray(buffer, 0, bytesRead))
                .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 && response.statusCode() != 206) {
                throw new RuntimeException("Upload failed: " + response.statusCode());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}