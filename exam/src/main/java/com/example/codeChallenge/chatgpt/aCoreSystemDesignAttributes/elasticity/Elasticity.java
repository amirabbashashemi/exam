package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.elasticity;

import codeChallenge.chatgpt.eCommon.ImageRequest;

import java.util.List;
import java.util.concurrent.*;


public class Elasticity {

    public Elasticity() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    // Thread pool برای CPU-bound tasks (fixed برای کنترل cores)
    private static final ExecutorService CPU_EXECUTOR = new ThreadPoolExecutor(
            5,
            4 * Runtime.getRuntime().availableProcessors(),
            1L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100));

    // Virtual Thread Executor برای I/O-bound work
    private static final ExecutorService IO_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    public void processImages(List<ImageRequest> requests) {
        for (var request : requests) {
            processSingle(request);
        }
    }

    private void processSingle(ImageRequest request) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            // I/O-heavy pre-processing (e.g., read image from disk/network)
            var ioTask = scope.fork(() -> IO_EXECUTOR.submit(() -> ioPreProcess(request)).get());

            // CPU-heavy processing
            var cpuTask = scope.fork(() -> CPU_EXECUTOR.submit(() -> cpuProcess(request)).get());

            scope.join();
            scope.throwIfFailed();

        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error processing image " + request, e);
        }
    }

    // I/O-heavy task
    private void ioPreProcess(ImageRequest request) {
        // read from disk/network, e.g., load image bytes
    }

    // CPU-heavy task
    private void cpuProcess(ImageRequest request) {
        // heavy image processing
    }

    public void shutdown() {
        try {
            CPU_EXECUTOR.shutdown();
            CPU_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS);
            IO_EXECUTOR.shutdown();
            IO_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
