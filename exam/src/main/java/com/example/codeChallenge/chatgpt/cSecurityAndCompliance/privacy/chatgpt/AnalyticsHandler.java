package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.privacy.chatgpt;


import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.time.Instant;

public class AnalyticsHandler implements HttpHandler {

    private final Gson gson = new Gson();
    private final AnalyticsRepository repository =
            new AnalyticsRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        AnalyticsRequest request =
                gson.fromJson(
                        new String(exchange.getRequestBody().readAllBytes()),
                        AnalyticsRequest.class
                );

        String anonymizedUserId = Pseudonymizer.anonymize(request.getUserId());
        AnalyticsEvent event = new AnalyticsEvent(
                anonymizedUserId,
                request.getAction(),
                Instant.now().toEpochMilli()
        );

        repository.save(event);

        exchange.sendResponseHeaders(200, -1);
    }
}
/*
Data Minimization	✅ کامل
Purpose Limitation	✅ واضح
Pseudonymization	✅ قبل از ذخیره
No PII at Rest	✅
Retention Policy	✅
GDPR Readiness	✅
Re-identification Risk	حداقل
 */