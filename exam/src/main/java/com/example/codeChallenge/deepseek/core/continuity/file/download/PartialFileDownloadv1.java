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
import java.util.logging.Logger;

public class PartialFileDownloadv1 {
    private static final Logger LOGGER = Logger.getLogger("PartialFileDownload");
    private static final int BUFFER_SIZE = 8192;
    private static final String FILE_URL = "https://example.com/largeFile.zip";
    private final Path outputPath = Paths.get("largeFile.zip");

    public void downloadWithResume() throws IOException, InterruptedException {
        LOGGER.info("Begin of method downloadWithResume");

        long downloaded = getDownloadedSize();

        HttpRequest request = createRequest(downloaded);

        HttpResponse<InputStream> response = callService(request);

        parseResponse(response, downloaded);
    }

    private void parseResponse(HttpResponse<InputStream> response, long downloaded) throws IOException {
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

    private static HttpResponse<InputStream> callService(HttpRequest request) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        return response;
    }

    private HttpRequest createRequest(long downloaded) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(FILE_URL))
                .header("User-Agent", "Java HttpClient");

        if (downloaded > 0) {
            builder.header("Range", "bytes=" + downloaded + "-");
        }

        return builder.GET().build();
    }

    private long getDownloadedSize() throws IOException {
        long downloaded = 0;

        if (Files.exists(outputPath)) {
            downloaded = Files.size(outputPath);
            LOGGER.info("Found existing file: {}" + downloaded + " bytes downloaded.");
        }

        return downloaded;
    }

}