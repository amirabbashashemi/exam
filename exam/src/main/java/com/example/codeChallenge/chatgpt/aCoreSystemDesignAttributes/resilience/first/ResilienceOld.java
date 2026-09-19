package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.resilience.first;

import codeChallenge.chatgpt.eCommon.ExternalPriceClient;

import java.math.BigDecimal;

public class ResilienceOld {


    private ExternalPriceClient client;

    public BigDecimal getPrice(String productId) {
        return client.fetchPrice(productId);
    }

}
