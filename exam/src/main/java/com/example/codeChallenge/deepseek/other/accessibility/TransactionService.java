package com.example.codeChallenge.deepseek.other.accessibility;

class TransactionService {
    private final AccessibleLogger accessibleLogger;

    TransactionService() {
        this.accessibleLogger = new AccessibleLogger("app.log", LogLevel.DEBUG);
    }

    public void processTransaction(String id) throws Exception {
        try {
            accessibleLogger.debug("Begin of method processTransaction");
            //process;
            accessibleLogger.info(String.format("TRX:%s done successful.", id));
        } catch (Exception e) {
            accessibleLogger.error(String.format("An exception occurred in method processTransaction in trx:%s", id));
            throw new RuntimeException(e);
        }
    }
}