package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.scalabilityResilienceObservability;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;

public class RequestCounterRefactored {
    private static final Map<String, LongAdder> COUNTER = new ConcurrentHashMap<>();
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();

    public void increment(String userId) {
        EXECUTOR_SERVICE.submit(() ->
                COUNTER.computeIfAbsent(userId, k -> new LongAdder()).increment()
        );

    }

    public int getCount(String userId) {
        int count = COUNTER.get(userId).intValue();
        String format = String.format("the count of userId:%s id %d", userId, count);
        System.out.println(format);
        return COUNTER.get(userId).intValue();
    }

    private void shutDown() {
        Thread thread = new Thread(() -> EXECUTOR_SERVICE.shutdown());
        Runtime.getRuntime().addShutdownHook(thread);
    }
}

