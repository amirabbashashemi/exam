package com.example.codeChallenge.deepseek.core.availability.request;

public class Request {
    String userId;
    RequestType type;

    public RequestType getType() {
        return RequestType.LIGHT;
    }

    public String getUserId() {
        return userId;
    }
}
