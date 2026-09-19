package com.example.codeChallenge.deepseek.other.archiveability.nw;

import com.google.gson.Gson;
import ir.dotin.archiveability.Order;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

public class FileSystemArchiveStorage implements ArchiveStorage {
    private final String storageUri;
    private final Gson gson = new Gson();

    public FileSystemArchiveStorage(String storageUri) {
        this.storageUri = storageUri;
    }

    @Override
    public Order retrieval(String id) {
        Path path = Path.of(storageUri);

        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8);

            return bufferedReader.lines()
                    .map(line -> gson.fromJson(line, Order.class))
                    .filter(order -> order.getId().equals(id))
                    .findFirst()
                    .orElse(null);

        } catch (IOException e) {
            System.out.printf("IOException in method archive: %s", e.getMessage());
            return null;
        }
    }

    @Override
    public List<String> archiveOrdersOlderThan(List<Order> orders) {
        try {
            Path path = Path.of(storageUri);

            StringBuilder stringBuilder = new StringBuilder();
            orders.forEach(order -> {
                order.setStatus("ARCHIVED");
                String json = gson.toJson(order);
                stringBuilder
                        .append(json)
                        .append("\n");
            });

            Files.writeString(path,
                    stringBuilder.toString(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            return orders.stream().map(Order::getId).toList();
        } catch (IOException e) {
            System.out.printf("IOException in method archive: %s", e.getMessage());
            return Collections.emptyList();
        }
    }

}
