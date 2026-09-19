package com.example.codeChallenge.excercise.exam1.authentication.mod;

import codeChallenge.excercise.exam1.authentication.User;

public interface Authenticator {
    Integer getType();

    void authenticate(User user);
}
