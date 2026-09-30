package com.example.codeChallenge.excercise.exam3.catalog;
/*
یک سرویس ProductCatalogService داریم که اطلاعات محصولات را از دیتابیس می‌خواند و به کاربران نمایش می‌دهد.
 در حال حاضر روزانه ۱۰۰۰ درخواست به این سرویس می‌رسد و هر درخواست حدود ۵۰۰ میلی‌ثانیه طول می‌کشد (چون هر بار به دیتابیس مراجعه می‌کند).
  پیش‌بینی می‌شود در فصل تخفیف‌ها، تعداد درخواست‌ها به ۱۰۰,۰۰۰ درخواست در روز برسد و هر کاربر ممکن است چندین بار یک محصول را ببیند.
   با کد فعلی، دیتابیس به گلوگاه تبدیل می‌شود و زمان پاسخ به شدت افزایش می‌یابد، به‌طوری که برخی درخواست‌ها بیش از ۱۰ ثانیه طول می‌کشند و کاربران صفحه را ترک می‌کنند.
 */

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

public class ProductCatalogService {
    private final Semaphore semaphore;
    private final Map<Long, Product> idempotencyProductMap = new ConcurrentHashMap<>();
    private final Map<Long, CompletableFuture<Product>> inflightMap = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private final ScheduledExecutorService scheduledExecutorService;

    public ProductCatalogService(int schedulePollSize, int dbConcurrencySize) {
        semaphore = new Semaphore(dbConcurrencySize);
        scheduledExecutorService = Executors.newScheduledThreadPool(schedulePollSize);
        scheduledExecutorService.scheduleAtFixedRate(this::refreshCache, 0, 2, TimeUnit.MINUTES);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public Product getProduct(Long productId) {
        Product productInIdempotency = getFromIdempotencyMap(productId);
        if (Objects.nonNull(productInIdempotency)) {
            return productInIdempotency;
        }

        CompletableFuture<Product> completableFuture = inflightMap.computeIfAbsent(productId,
                (id) -> CompletableFuture.supplyAsync(() -> {
                    boolean acquired = false;
                    try {
                        acquired = semaphore.tryAcquire(20, TimeUnit.SECONDS);

                        if (!acquired) {
                            throw new RuntimeException("maximum concurrent database load achieved.");
                        }

                        Product product = fetchFromDatabase(productId);

                        addToIdempotencyMap(product);

                        return product;
                    } catch (Exception e) {
                        System.err.printf("An Exception occurred in method fetchFromDatabase. error message is : %s", e.getMessage());
                        throw new RuntimeException(e);
                    } finally {
                        if (acquired) {
                            semaphore.release();
                        }
                    }

                }, executorService));

        try {
            return completableFuture.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch product: " + productId, e);
        } finally {
            inflightMap.remove(productId, completableFuture);
        }
    }

    private Product fetchFromDatabase(Long productId) {
        // شبیه‌سازی کوئری دیتابیس
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            throw new RuntimeException("Error in method fetchFromDatabase. error message is : " + e.getMessage());
        }
        return new Product(productId, "Product " + productId);
    }

    private Product getFromIdempotencyMap(Long id) {
        return idempotencyProductMap.get(id);
    }

    private void addToIdempotencyMap(Product product) {
        product.setCacheTime(new Date());
        idempotencyProductMap.putIfAbsent(product.getProductId(), product);
    }

    private void refreshCache() {
        Date now = new Date();
        Date from = Date.from(now.toInstant().minus(5, ChronoUnit.MINUTES));

        idempotencyProductMap
                .entrySet()
                .removeIf(entry -> entry.getValue().cacheTime.before(from));
    }

    private void shutdown() {
        try {
            shutdownExecutor(executorService);
            shutdownExecutor(scheduledExecutorService);
        } catch (InterruptedException e) {
            System.err.printf("Error in method fetchWithExecutor. error message is : %s", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void shutdownExecutor(ExecutorService executorService) throws InterruptedException {
        executorService.shutdown();
        boolean terminated = executorService.awaitTermination(20, TimeUnit.SECONDS);
        if (!terminated) {
            executorService.shutdownNow();
        }
    }
}