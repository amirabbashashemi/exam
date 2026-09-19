package com.example.codeChallenge.deepseek.architectureQualityAttributes.testability.nw.factory;

import codeChallenge.deepseek.architectureQualityAttributes.testability.nw.TestModeEnum;
import codeChallenge.deepseek.architectureQualityAttributes.testability.nw.repo.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class UserRepositoryFactory {
    private static final Map<TestModeEnum, UserRepository> userRepositoryMap = new HashMap<>();

    public UserRepositoryFactory() {
        ServiceLoader<UserRepository> userRepositories = ServiceLoader.load(UserRepository.class);

        userRepositories.forEach(userRepository -> this.userRepositoryMap.put(userRepository.type(), userRepository));

    }

    public static UserRepository anInstance(TestModeEnum testModeEnum) {
        UserRepository userRepository = userRepositoryMap.get(testModeEnum);

        if (userRepository == null) {
            throw new IllegalArgumentException(testModeEnum.name() + " not supported");
        }

        return userRepository;
    }
}
