package com.example.codeChallenge.deepseek.architectureQualityAttributes.testability.nw.repo;

import codeChallenge.deepseek.architectureQualityAttributes.testability.nw.TestModeEnum;

public interface UserRepository {
    TestModeEnum type();

    void save(String username, String email);
}
