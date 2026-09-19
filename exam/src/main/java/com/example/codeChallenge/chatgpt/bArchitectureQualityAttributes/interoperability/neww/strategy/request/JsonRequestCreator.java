package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.interoperability.neww.strategy.request;

import codeChallenge.chatgpt.eCommon.User;

public class JsonRequestCreator implements RequestCreator {
    @Override
    public String create(User user) {
        return "{ \"name\": \"" + user.getName() + "\", \"age\": " + user.getAge() + " }";
    }
}
