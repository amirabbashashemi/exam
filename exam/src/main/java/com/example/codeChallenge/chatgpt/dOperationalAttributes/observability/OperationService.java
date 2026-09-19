package com.example.codeChallenge.chatgpt.dOperationalAttributes.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class OperationService {
    private static final AtomicInteger INDEX = new AtomicInteger(0);
    private static final Map<Integer, Long> LAST_DURATION_MAP = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(OperationService.class);
    private final static MeterRegistry meterRegistry = new SimpleMeterRegistry();

    private final static Counter successfullCounter = Counter.builder("execute.success")
            .baseUnit("successful execution")
            .register(meterRegistry);

    private final static Counter failedCounter = Counter.builder("execute.failed")
            .baseUnit("failed execution")
            .register(meterRegistry);

    private final static Gauge gaugeMetric = Gauge.builder("gauge", () -> getAverage())
            .baseUnit("gauge metric")
            .register(meterRegistry);


    private static Long getAverage() {
        Long reduce = LAST_DURATION_MAP.values().stream().reduce(0L, (a, b) -> a + b);
        return reduce / LAST_DURATION_MAP.size();
    }

    public void execute(String action) {
        long start = System.currentTimeMillis();
        LOGGER.info("Method execute started at {}", start);
        try {

            doBusiness(action);
            successfullCounter.increment();
            long duration = System.currentTimeMillis() - start;
            LOGGER.info("Method execute ended SUCCESS at {} duration {}.", start, duration);
            int nextIndex = getNextIndex();
            LAST_DURATION_MAP.put(nextIndex, duration);
        } catch (Exception e) {
            LOGGER.error("Method execute ended FAILED at {} duration {}.", start, (System.currentTimeMillis() - start));
            throw new RuntimeException(STR."An exception occurred in method execute in action \{action}", e);
        }
    }

    private void doBusiness(String action) {
        if (action.equals("FAIL")) {
            failedCounter.increment();
            throw new RuntimeException("An exception occurred in method doBusiness");
        }
    }

    private static int getNextIndex() {
        int lastIndex = INDEX.getAndAdd(1);

        if (lastIndex > 999) {
            INDEX.set(0);
        }

        return lastIndex;
    }
}