package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.maintainability.strategy;

import codeChallenge.chatgpt.eCommon.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgeValidator implements Validator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgeValidator.class);

    @Override
    public void validate(User user) {
        if (user.getAge() < 18) {
            LOGGER.error("age {} is not invalid for user {}",  user.getAge(), user.toString());
            throw new RuntimeException("Under age");
        }
    }
}
