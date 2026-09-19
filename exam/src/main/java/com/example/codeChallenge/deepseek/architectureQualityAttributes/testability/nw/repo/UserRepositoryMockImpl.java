package com.example.codeChallenge.deepseek.architectureQualityAttributes.testability.nw.repo;

import codeChallenge.deepseek.architectureQualityAttributes.testability.nw.TestModeEnum;

public class UserRepositoryMockImpl implements UserRepository {

    @Override
    public TestModeEnum type() {
        return TestModeEnum.TEST;
    }

    @Override
    public void save(String username, String email) {
        System.out.println("Mock: Saved to DB: " + username);
        // ممکن است خطای Connection پرتاب کند!
    }
}
