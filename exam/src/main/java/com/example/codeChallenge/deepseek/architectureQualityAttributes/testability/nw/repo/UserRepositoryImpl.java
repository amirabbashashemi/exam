package com.example.codeChallenge.deepseek.architectureQualityAttributes.testability.nw.repo;

import codeChallenge.deepseek.architectureQualityAttributes.testability.nw.TestModeEnum;

import java.util.HashMap;
import java.util.Map;

public class UserRepositoryImpl implements UserRepository {
    private final Map<String, String> store = new HashMap<>();

    @Override
    public TestModeEnum type() {
        return TestModeEnum.MAIN;
    }

    @Override
    public void save(String username, String email) {
        store.put(username, email);
        System.out.println("Saved to DB: " + username);
        // ممکن است خطای Connection پرتاب کند!
    }
}
