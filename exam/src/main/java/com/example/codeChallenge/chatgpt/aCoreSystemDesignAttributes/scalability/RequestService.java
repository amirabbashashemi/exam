package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.scalability;

import com.google.gson.Gson;
import com.sun.net.httpserver.Request;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RequestService {
    private static final Gson GSON = new Gson();
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            5,
            4 * Runtime.getRuntime().availableProcessors(),
            1L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100)
    );

    public RequestService() {
        addShutdownHook();
    }

    public void handle(Request request) {
        try {
            saveToActiveMQ(request);
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred in method handle.", e);
        }
    }

    private void onMessage(Request request) {
        if (Objects.nonNull(request)) {
            EXECUTOR_SERVICE.submit(() -> process(request));
        }
    }

    private void saveToActiveMQ(Request request) {
        try {
            String requestString = GSON.toJson(request);
            //save to queue: request-worker-input
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred in method saveToFile.", e);
        }

    }

    private void process(Request request) {
        // heavy CPU + I/O work
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                EXECUTOR_SERVICE.shutdown();
                EXECUTOR_SERVICE.awaitTermination(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new RuntimeException("An exception occurred in method terminateExecutor.", e);
            }
        }));
    }

}
