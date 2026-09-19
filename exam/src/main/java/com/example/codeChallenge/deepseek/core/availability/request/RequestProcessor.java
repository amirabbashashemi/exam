package com.example.codeChallenge.deepseek.core.availability.request;

import codeChallenge.deepseek.core.availability.request.strategy.RequestProcessorService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class RequestProcessor {
    private final RequestProcessorService requestProcessorService = new RequestProcessorService();
    private ExecutorService EXECUTOR_SERVICEFOR_HEAVY =  Executors.newFixedThreadPool(5);
    private ExecutorService EXECUTOR_SERVICEFOR_LIGHT =  Executors.newFixedThreadPool(20);
    private ExecutorService EXECUTOR_SERVICEFOR_NORMAL =  Executors.newFixedThreadPool(10);

    public RequestProcessor() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void processRequest(Request request) {
        ExecutorService executorService = gerExecutorService(request);
        requestProcessorService.process(request, executorService);
    }

    public void shutdown() {
        shutdown(EXECUTOR_SERVICEFOR_HEAVY);
        shutdown(EXECUTOR_SERVICEFOR_LIGHT);
        shutdown(EXECUTOR_SERVICEFOR_NORMAL);
    }

    public void shutdown(ExecutorService executorService) {
        try {
            executorService.shutdown();
            boolean terminated = executorService.awaitTermination(30, TimeUnit.SECONDS);
            if (!terminated) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.printf("Error in terminating. error message is : %s", e.getMessage());
        }
    }

    private ExecutorService gerExecutorService(Request request) {
        if (RequestType.HEAVY.equals(request.getType())) {
            return EXECUTOR_SERVICEFOR_HEAVY;
        } else if (RequestType.LIGHT.equals(request.getType())) {
            return EXECUTOR_SERVICEFOR_LIGHT;
        } else {
            return EXECUTOR_SERVICEFOR_NORMAL;
        }
    }
}

