package com.example.codeChallenge.deepseek.architectureQualityAttributes.testability.nwV2.re;

public class StubUserRepository implements UserRepository {

    @Override
    public void save(String username, String email) {
        System.out.println("Saved to DB: " + username);
    }
}
