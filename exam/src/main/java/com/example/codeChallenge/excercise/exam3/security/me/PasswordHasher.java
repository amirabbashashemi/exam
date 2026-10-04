package com.example.codeChallenge.excercise.exam3.security.me;

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
//
//
//    public static void main(String[] args) {
//        String password = "mySecurePassword123";
//
//        // Hash the password
//        String hashedPassword = hashPassword(password);
//
//        System.out.println("Hashed Password: " + hashedPassword);
//    }
}