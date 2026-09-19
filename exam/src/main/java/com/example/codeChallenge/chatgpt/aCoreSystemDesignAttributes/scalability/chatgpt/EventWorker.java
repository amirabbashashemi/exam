package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.scalability.chatgpt;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EventWorker {

    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(
            5,
            4 * Runtime.getRuntime().availableProcessors(),
            1,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000)
    );


    public void submit(Runnable task) {
        EXECUTOR.submit(task);
    }

}
