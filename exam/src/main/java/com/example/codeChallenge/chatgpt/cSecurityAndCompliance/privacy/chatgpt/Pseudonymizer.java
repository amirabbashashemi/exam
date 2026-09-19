package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.privacy.chatgpt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class Pseudonymizer {

    private static final String DAILY_SALT = "rotate-me-daily";

    public static String anonymize(Long userId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((userId + DAILY_SALT).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Cannot anonymize user", e);
        }
    }
}
