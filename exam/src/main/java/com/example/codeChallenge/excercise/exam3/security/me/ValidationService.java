package com.example.codeChallenge.excercise.exam3.security.me;

public class ValidationService {

    public void validate(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("username or password is invalid.");
        }

        if (username.length() > 20 || password.length() > 50 || password.length() < 8) {
            throw new IllegalArgumentException("username or password is invalid.");
        }
    }

}
