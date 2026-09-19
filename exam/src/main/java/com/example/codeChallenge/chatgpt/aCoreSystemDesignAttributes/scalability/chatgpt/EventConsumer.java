package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.scalability.chatgpt;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

public class EventConsumer {
    private static final Path PATH = Path.of("events.log");

    public void consume(Consumer<String> handler) {
        try (var reader = Files.newBufferedReader(PATH)) {

            String string = reader.readLine();
            if (!Objects.isNull(string)) {

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
