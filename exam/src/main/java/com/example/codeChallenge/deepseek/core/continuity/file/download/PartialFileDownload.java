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

public class PartialFileDownload {
    private static final int BUFFER_SIZE = 8192;
    private static final String FILE_URL = "https://example.com/largeFile.zip";
    private static final Path OUTPUT_PATH = Paths.get("largeFile.zip");

    public void downloadWithResume() throws IOException, InterruptedException {
        System.out.println("Begin of method downloadWithResume");

        long downloadedSize = getDownloadedSize();

        HttpRequest request = createRequest(downloadedSize);

        HttpResponse<InputStream> response = callService(request);

        parseResponse(response);

        System.out.println("End of method downloadWithResume");
    }

    public long getDownloadedSize() {
        long size = 0;
        try {
            boolean exists = Files.exists(OUTPUT_PATH);
            if (exists) {
                long fileSize = Files.size(OUTPUT_PATH);
                size = Math.max(fileSize, size);
            }
        } catch (IOException e) {
            System.err.println("IOException in method getDownloadedSize");
            throw new RuntimeException(e);
        }
        return size;
    }

    private HttpRequest createRequest(long downloadedSize) {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(FILE_URL))
                .header("User-Agent", "Java HttpClient");

        if (downloadedSize > 0) {
            builder.header("Range", "bytes=" + downloadedSize + "-");
        }

        return builder.GET().build();
    }

    private HttpResponse<InputStream> callService(HttpRequest httpRequest) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            return client.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        } catch (IOException e) {
            System.err.println("IOException in method callService");
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            System.err.println("InterruptedException in method callService");
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private void parseResponse(HttpResponse<InputStream> response) throws IOException {
        int status = response.statusCode();
        if (status == 200 || status == 206) {
            try (
                    InputStream inputStream = response.body();
                    FileOutputStream fileOutputStream = new FileOutputStream(OUTPUT_PATH.toFile())) {
                int size;
                byte[] buffer = new byte[BUFFER_SIZE];

                while ((size = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, size);
                }
            }
        } else {
            throw new IOException("An exception occurred in method parseResponse");
        }
    }

}