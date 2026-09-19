package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.interoperability.neww.strategy.request;

import codeChallenge.chatgpt.eCommon.User;

import java.util.Map;
import java.util.Objects;

public class RequestCreatorContext {
    private final Map<RequestType, RequestCreator> requestCreatorMap;

    public RequestCreatorContext(Map<RequestType, RequestCreator> requestCreator) {
        this.requestCreatorMap = requestCreator;
    }

    public String makeRequest(RequestType requestType, User user) {
        RequestCreator requestCreator = this.requestCreatorMap.get(requestType);

        if (Objects.nonNull(requestCreator)) {
            return requestCreator.create(user);
        } else {
            throw new RuntimeException("requestType is not valid");
        }
    }
}
