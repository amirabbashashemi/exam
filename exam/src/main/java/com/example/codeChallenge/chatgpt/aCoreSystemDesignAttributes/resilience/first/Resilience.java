package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.resilience.first;

import codeChallenge.chatgpt.eCommon.ExternalPriceClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Resilience {

    private ExternalPriceClient client;

    private static final CircuitBreakerConfig CIRCUIT_BREAKER_CONFIG = CircuitBreakerConfig.custom()
            .failureRateThreshold(50.0f)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .permittedNumberOfCallsInHalfOpenState(2)
            .slidingWindowSize(10)
            .minimumNumberOfCalls(5)
            .recordExceptions(Exception.class)
            .build();

    private static final CircuitBreaker CIRCUIT_BREAKER = CircuitBreaker.of("fetchPrice", CIRCUIT_BREAKER_CONFIG);

    public BigDecimal getPrice(String productId) {
        long startTime = System.nanoTime();
        BigDecimal bigDecimal = null;
        if (CIRCUIT_BREAKER.tryAcquirePermission()) {
            try {
                bigDecimal = client.fetchPrice(productId);
                long duration = System.nanoTime() - startTime;
                CIRCUIT_BREAKER.onSuccess(duration, TimeUnit.NANOSECONDS);
            } catch (Exception exception) {
                long duration = System.nanoTime() - startTime;
                CIRCUIT_BREAKER.onError(duration, TimeUnit.NANOSECONDS, exception);
                throw new RuntimeException(exception);
            }
        } else {
            bigDecimal = fallBack();
        }
        return bigDecimal;
    }

    private BigDecimal fallBack() {
        return BigDecimal.ZERO;
    }

}
/*
سوالات:

اگر سرویس خارجی برای مدتی down شود، چه اتفاقی برای سیستم شما می‌افتد؟
این کد چه مشکلاتی از نظر Resilience دارد؟
چگونه این کد را طوری تغییر می‌دهید که:
سیستم شما کاملاً down نشود
فشار غیرضروری به سرویس خارجی وارد نشود
پس از بازگشت سرویس خارجی، سیستم به حالت عادی برگردد
اگر مجبور باشید فقط با Pure Java (بدون library) این مشکل را حل کنید، چه می‌کنید؟
 */