package com.example.codeChallenge.deepseek.security.security.me;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    private final int saltLength;  // Increasing this value makes it more secure, but slower

    public PasswordHasher(int saltLength) {
        this.saltLength = saltLength;
    }

    public UserDto hashPassword(String username, String password) {
        String salt = BCrypt.gensalt(saltLength);

        String hashPassword = BCrypt.hashpw(password, salt);

        return new UserDto(username, hashPassword, salt);
    }

    public boolean checkPassword(String password, String storedPassword) {
        return BCrypt.checkpw(password, storedPassword);
    }

}