package com.example.codeChallenge.chatgpt.dOperationalAttributes.observability;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class OperationServiceGPT {
    private static final Logger LOGGER = LoggerFactory.getLogger(OperationService.class);
    private final Timer executionTimer;
    private final MeterRegistry meterRegistry;

    public OperationServiceGPT(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.executionTimer = Timer.builder("operation.execute")
                .description("Execution time of operations")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(meterRegistry);
    }

    public void execute(String action) {
        long start = System.nanoTime();
        try {
            doBusiness(action);
            recordSuccess(start);
        } catch (Exception e) {
            recordFailure(start);
            throw e;
        }
    }

    private void doBusiness(String action) {
        if ("FAIL".equals(action)) {
            throw new RuntimeException("Business failure");
        }
    }

    private void recordSuccess(long startNano) {
        long duration = System.nanoTime() - startNano;
        executionTimer.record(duration, TimeUnit.NANOSECONDS);

        meterRegistry.counter(
                "operation.result",
                "status", "success"
        ).increment();

        LOGGER.info("Operation SUCCESS in {} ms", duration / 1_000_000);
    }

    private void recordFailure(long startNano) {
        long duration = System.nanoTime() - startNano;
        executionTimer.record(duration, TimeUnit.NANOSECONDS);

        meterRegistry.counter(
                "operation.result",
                "status", "failed"
        ).increment();

        LOGGER.error("Operation FAILED in {} ms", duration / 1_000_000);
    }
}

