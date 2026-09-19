package com.example.codeChallenge.deepseek.core.continuity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class PartialFileDownload {
    private static final int RETRY_COUNT = 5;
    private static final int BUFFER_SIZE = 8192;

    public void downloadWithResume(String downloadFileUrl, Path outputPath) throws IOException, InterruptedException {
        long downloaded = getDownloadedSize(outputPath);

        HttpRequest httpRequest = createHttpRequest(downloadFileUrl, downloaded);

        callExternalServiceWithRetry(httpRequest, downloadFileUrl, outputPath, downloaded);

    }

    private long getDownloadedSize(Path outputPath) throws IOException {
        long downloaded = 0;

        if (Files.exists(outputPath)) {
            downloaded = Files.size(outputPath);
            System.out.println("Found existing file: " + downloaded + " bytes downloaded.");
        }

        return downloaded;
    }

    private static HttpRequest createHttpRequest(String downloadFileUrl, long downloadedSize) {
        try {
            URI uri = new URI(downloadFileUrl);
            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder().uri(uri);

            if (downloadedSize > 0) {
                httpRequestBuilder.header("Range", "bytes=" + downloadedSize + "-");
            }

            return httpRequestBuilder.build();
        } catch (URISyntaxException e) {
            System.out.printf("URISyntaxException in method createHttpRequest: %s", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void callExternalServiceWithRetry(HttpRequest httpRequest, String downloadFileUrl, Path outputPath, long downloaded) throws InterruptedException {
        Integer retry = 0;
        Long delay = 500L;

        while (retry < RETRY_COUNT) {
            try {
                HttpResponse<InputStream> response = callExternalService(httpRequest);

                parseResponse(outputPath, response, downloaded);

                return;
            } catch (InterruptedException e) {
                System.out.printf("InterruptedException in method callExternalServiceWithRetry : %s", e.getMessage());
                Thread.currentThread().interrupt();
                delay = (long) (Math.pow(2, retry) * 500);
                retry++;
                Thread.sleep(delay);
            } catch (IOException e) {
                System.out.printf("IOException in method callExternalServiceWithRetry : %s", e.getMessage());
                delay = (long) (Math.pow(2, retry) * 500);
                retry++;
                Thread.sleep(delay);
            }

            try {
                downloaded = Files.exists(outputPath) ? Files.size(outputPath) : 0;
            } catch (IOException ignored) {
            }
        }

        System.out.printf("Max try attempt for file: %s", downloadFileUrl);
    }

    private static HttpResponse<InputStream> callExternalService(HttpRequest httpRequest) throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            return client.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        } catch (IOException e) {
            System.out.printf("An Exception occurred in method callExternalService : %s", e.getMessage());
            throw new IOException("An Exception occurred in method callExternalService", e);
        }
    }

    private static void parseResponse(Path outputPath, HttpResponse<InputStream> response, long downloaded) throws IOException {
        int responseCode = response.statusCode();

        if (responseCode == 200 || responseCode == 206) {
            int len = 0;
            long downloadedSize = downloaded > 0 && responseCode == 206 ? downloaded : 0;
            boolean appendMod = downloadedSize > 0 && responseCode == 206;

            byte[] bytes = new byte[BUFFER_SIZE];
            InputStream inputStream = response.body();

            try (FileOutputStream fileOutputStream = new FileOutputStream(outputPath.toFile(), appendMod)) {
                while ((len = inputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, len);

                    downloadedSize += len;
                    System.out.println(downloadedSize + " bytes downloaded.");
                }
            } catch (IOException e) {
                throw new IOException("IOException in methd parseResponse: " + e.getMessage());
            }

        } else {
            throw new RuntimeException("Bad response code: " + responseCode);
        }

    }
}