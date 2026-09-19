package com.example.codeChallenge.deepseek.core.availability.request.strategy;

import codeChallenge.deepseek.core.availability.request.Request;
import codeChallenge.deepseek.core.availability.request.RequestType;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class RequestProcessorService {

    private static final Map<RequestType, RequestProcessor> REQUEST_PROCESSOR_MAP = new ConcurrentHashMap<>();

    public RequestProcessorService() {
        ServiceLoader<RequestProcessor> requestProcessorServiceLoaders = ServiceLoader.load(RequestProcessor.class);

        requestProcessorServiceLoaders.forEach(requestProcessor -> REQUEST_PROCESSOR_MAP.put(requestProcessor.getRequestType(), requestProcessor));
    }

    public void process(Request request, ExecutorService executorService) {
        RequestProcessor requestProcessor = REQUEST_PROCESSOR_MAP.get(request.getType());
        requestProcessor.process(request, executorService);
    }
}
