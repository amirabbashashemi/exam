package com.example.codeChallenge.chatgpt.eCommon.repo;

import codeChallenge.chatgpt.eCommon.User;
import codeChallenge.chatgpt.eCommon.UserDto;
import codeChallenge.chatgpt.cSecurityAndCompliance.security.neww.UserDtoBuilder;

public class UserRepository {
    public void save(User user) {

    }

    public User findById(String userId) {
        return new User();
    }

    public UserDto findById(Long userId) {
        User user = new User();

        return UserDtoBuilder.anInstance()
                .withId(user.getId())
                .withName(user.getName())
                .withEmail(user.getEmail())
                .build();
    }

}
