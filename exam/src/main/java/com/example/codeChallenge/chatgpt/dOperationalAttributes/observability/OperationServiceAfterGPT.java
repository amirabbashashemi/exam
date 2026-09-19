package com.example.codeChallenge.chatgpt.dOperationalAttributes.observability;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.concurrent.TimeUnit;

public class OperationServiceAfterGPT {
    private final MeterRegistry meterRegistry;
    private final Timer executionTimer;

    public OperationServiceAfterGPT(MeterRegistry meterRegistry, Timer executionTimer) {
        this.meterRegistry = meterRegistry;
        this.executionTimer = executionTimer;
    }


    public void execute(String action) {
        long start = System.currentTimeMillis();

        try {
            doBusiness(action, start);
            recordSuccess(start);
        } catch (Exception e) {
            recordFailed(start);
        }
    }

    private void recordSuccess(long start) {
        long duration = System.nanoTime() - start;
        executionTimer.record(duration, TimeUnit.NANOSECONDS);

        meterRegistry.counter("operation result", "success").increment();

        executionTimer.record(duration, TimeUnit.NANOSECONDS);
    }

    private void recordFailed(long start) {
        long duration = System.nanoTime() - start;
        executionTimer.record(duration, TimeUnit.NANOSECONDS);

        meterRegistry.counter("operation result", "failed").increment();

        executionTimer.record(duration, TimeUnit.NANOSECONDS);
    }

    private void doBusiness(String action, long start) {
        if (action.equals("FAIL")) {
            throw new RuntimeException();
        }
    }

}