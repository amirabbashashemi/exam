package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.maintainability.strategy;

import codeChallenge.chatgpt.eCommon.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailValidator implements Validator {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailValidator.class);

    @Override
    public void validate(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            LOGGER.error("email {} is not valid for user {}", user.getEmail(), user.toString());
            throw new RuntimeException("Invalid email");
        }
    }

}
