package com.example.codeChallenge.deepseek.security.security.me;

public class ValidationService {
    String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    public void validate(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("username or password is invalid.");
        }

        if (username.length() > 20 || password.length() > 50 || password.length() < 8) {
            throw new IllegalArgumentException("username or password is invalid.");
        }

        boolean matches = pattern.matches(password);
        if (!matches) {
            throw new IllegalArgumentException("password pattern is invalid.");
        }

    }

}
