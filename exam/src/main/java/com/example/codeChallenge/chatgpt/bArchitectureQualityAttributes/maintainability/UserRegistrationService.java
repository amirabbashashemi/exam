package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.maintainability;

import codeChallenge.chatgpt.eCommon.User;
import codeChallenge.chatgpt.eCommon.repo.UserRepository;

public class UserRegistrationService {

    private UserRepository userRepository;

    public void register(User user) {
        try {
            if (user.getEmail() == null || !user.getEmail().contains("@")) {
                throw new RuntimeException("Invalid email");
            }

            if (user.getPassword().length() < 8) {
                throw new RuntimeException("Weak password");
            }

            if (user.getAge() < 18) {
                throw new RuntimeException("Under age");
            }

            userRepository.save(user);

            System.out.println("User registered successfully");

        } catch (Exception e) {
            System.err.println("Error in register method: " + e.getMessage());
            throw e;
        }
    }
}
