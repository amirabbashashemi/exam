package com.example.codeChallenge.deepseek.core.availability.request.strategy;

import codeChallenge.deepseek.core.availability.request.Request;
import codeChallenge.deepseek.core.availability.request.RequestType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class NormalRequestProcessor implements RequestProcessor {
    private static final Map<String, List<Request>> HISTORY = new ConcurrentHashMap<>();

    @Override
    public RequestType getRequestType() {
        return RequestType.NORMAL;
    }

    @Override
    public void process(Request request, ExecutorService executorService) {
        CompletableFuture
                .supplyAsync(() -> {
                    this.processNormal(request);
                    HISTORY.computeIfAbsent(request.getUserId(), k -> new ArrayList<>()).add(request);
                    return request;
                }, executorService);
    }

    private void processNormal(Request request) {
        try {
            Thread.sleep(1000); // ۱ ثانیه
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.printf("Error in processing heavy for request %s%n", request.getType());
        }
    }
}
