package com.example.codeChallenge.deepseek.architectureQualityAttributes.testability.nwV2.re;

import java.util.HashMap;
import java.util.Map;

public class RealUserRepository implements UserRepository {
    private final Map<String, String> store = new HashMap<>();

    @Override
    public void save(String username, String email) {
        store.put(username, email);
        System.out.println("Saved to DB: " + username);
        // ممکن است خطای Connection پرتاب کند!
    }
}
