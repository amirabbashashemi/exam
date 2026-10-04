package com.example.codeChallenge.deepseek.core.availability.request.strategy;

import com.example.codeChallenge.deepseek.core.availability.request.RequestType;

public class Request {
    String userId;
    com.example.codeChallenge.deepseek.core.availability.request.RequestType type;

    public com.example.codeChallenge.deepseek.core.availability.request.RequestType getType() {
        return RequestType.LIGHT;
    }

    public String getUserId() {
        return userId;
    }
}
