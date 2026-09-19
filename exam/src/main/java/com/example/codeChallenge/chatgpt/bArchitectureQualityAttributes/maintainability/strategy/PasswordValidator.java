package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.maintainability.strategy;

import codeChallenge.chatgpt.eCommon.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordValidator implements Validator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordValidator.class);

    @Override
    public void validate(User user) {
        if (user.getPassword().length() < 8) {
            LOGGER.error("password *** is weak for user {}", user.toString());
            throw new RuntimeException("Weak password");
        }
    }
}
