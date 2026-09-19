package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.elasticity;

import codeChallenge.chatgpt.eCommon.ImageRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ElasticityOld {
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void processImage(ImageRequest request) {
        executorService.submit(() -> {
            // heavy CPU + I/O work
            process(request);
        });
    }

    private void process(ImageRequest request) {
        // CPU-intensive image processing
    }
}

/*
وقتی ترافیک کم است → اکثر threadها idle هستند → resource هدر می‌رود

وقتی ترافیک زیاد است → queue پر می‌شود → requests صف می‌کشند → latency بالا می‌رود

❓ سوال‌های امتحان

مشکل elasticity در کد بالا چیست؟

چگونه می‌توان با pure Java 21 سیستم را elastic کرد؟

اگر تعداد requestها ناگهانی ۱۰ برابر شود، چه راهکاری برای جلوگیری از overload وجود دارد؟

trade-offهای استفاده از Virtual Thread vs ThreadPoolExecutor برای این سیستم چیست؟

چگونه می‌توان resource usage را در زمان کم بودن ترافیک بهینه کرد؟
 */