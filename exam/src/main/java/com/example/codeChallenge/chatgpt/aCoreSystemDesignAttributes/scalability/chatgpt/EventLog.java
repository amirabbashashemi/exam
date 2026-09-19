package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.scalability.chatgpt;

import com.google.gson.Gson;
import com.sun.net.httpserver.Request;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class EventLog {
    private static final Gson GSON = new Gson();
    private static final Path PATH = Path.of("events.log");

    public synchronized void append(Request request) {
        try {
            String eventJson = GSON.toJson(request);

            Files.writeString(
                    PATH,
                    eventJson,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to append event", e);
        }

    }

}
