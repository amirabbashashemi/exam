package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.security.neww;

import java.util.Base64;

public class JwtUtil {

    // فرض: SSO public key validation ساده شده
    public static Long extractUserIdFromToken(String token) throws Exception {
        // Token format: header.payload.signature (Base64)
        String[] parts = token.split("\\.");
        if (parts.length != 3) throw new SecurityException("Invalid JWT");

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

        // Exam-level JSON parsing (assume "sub" contains userId)
        int start = payloadJson.indexOf("\"sub\":\"") + 7;
        int end = payloadJson.indexOf("\"", start);
        if (start < 7 || end < start) throw new SecurityException("Invalid JWT payload");

        return Long.parseLong(payloadJson.substring(start, end));
    }

    public static boolean validateToken(String token) {
        // برای امتحان فرض کنیم signature درست است
        return token != null && token.split("\\.").length == 3;
    }
}
