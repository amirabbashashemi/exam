package com.example.codeChallenge.deepseek.core.scalability;

    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    import java.nio.file.StandardOpenOption;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.concurrent.*;

    public class LogService {
        private static volatile boolean RUNNING = true;
        private static volatile boolean IS_SHUTTING_DOWN = false;
        private static final int BATCH_SIZE = 20;
        private static final int bufferSize = 100000;
        private static final String LOG_FILE = "app.log";
        private static final BlockingQueue<String> BUFFER_QUEUE = new ArrayBlockingQueue<>(bufferSize);
        private static final ExecutorService CONSUMER_EXECUTOR = Executors.newSingleThreadExecutor();

        public LogService() {
            startConsumer();

            Runtime.getRuntime().addShutdownHook(
                    new Thread(() ->
                            shutdown()
                    ));
        }

        public void log(String message) {
            boolean hasCapacity = BUFFER_QUEUE.offer(message);

            if (!hasCapacity) {
                System.out.println("BUFFER_QUEUE is full. bufferSize is: " + bufferSize);
            }
        }

        private void startConsumer() {
            try {
                CONSUMER_EXECUTOR.submit(() -> {
                    try {
                        consumeMessage();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                });
            } catch (RuntimeException runtimeException) {
                throw new RuntimeException(runtimeException);
            }
        }

        private void consumeMessage() throws InterruptedException {
            List<String> messages = new ArrayList<>();
            while (RUNNING) {
                try {
                    String poll = BUFFER_QUEUE.poll(10, TimeUnit.SECONDS);
                    if (poll != null) {
                        messages.add(poll);

                        BUFFER_QUEUE.drainTo(messages, BATCH_SIZE - 1);

                        saveToFile(messages);

                        messages.clear();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            if (!IS_SHUTTING_DOWN) {
                saveRemainToFile();
            }
        }

        private void saveRemainToFile() {
            List<String> messages = new ArrayList<>();

            BUFFER_QUEUE.drainTo(messages);

            saveToFile(messages);
        }

        private void saveToFile(List<String> messages) {
            if (!messages.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();

                messages.forEach(message -> {
                    stringBuilder.append(java.time.LocalDateTime.now())
                            .append(" - ")
                            .append(message)
                            .append("\n");
                });

                try {
                    Files.write(
                            Paths.get(LOG_FILE),
                            stringBuilder.toString().getBytes(),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND);
                } catch (IOException ioException) {
                    throw new RuntimeException(ioException);
                }

            }
        }

        private void shutdown() {
            try {
                IS_SHUTTING_DOWN = true;
                RUNNING = false;
                saveRemainToFile();

                CONSUMER_EXECUTOR.shutdown();
                if (!CONSUMER_EXECUTOR.awaitTermination(2, TimeUnit.SECONDS)) {
                    CONSUMER_EXECUTOR.shutdownNow();
                }
            } catch (InterruptedException interruptedException ) {
                throw new RuntimeException(interruptedException);
            }

        }

    }
