package com.example.codeChallenge.deepseek.core.availability.request.strategy;

import codeChallenge.deepseek.core.availability.request.Request;
import codeChallenge.deepseek.core.availability.request.RequestType;

import java.util.concurrent.ExecutorService;

public interface RequestProcessor {
    RequestType getRequestType();

    void process(Request request, ExecutorService executorService);

}
