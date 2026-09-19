package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.robustness;

import codeChallenge.chatgpt.eCommon.ExternalEmailValidationClient;
import codeChallenge.chatgpt.eCommon.User;
import codeChallenge.chatgpt.eCommon.repo.UserRepository;

public class UserRegistrationServiceOld {

    private ExternalEmailValidationClient externalEmailValidationClient;
    private UserRepository userRepository;

    public void register(User user) {
        boolean isValid = externalEmailValidationClient.validateEmail(user.getEmail());

        if (!isValid) {
            throw new RuntimeException("Invalid email");
        }

        userRepository.save(user);
    }
}
