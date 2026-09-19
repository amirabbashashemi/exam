package com.example.codeChallenge.excercise.exam1.authentication.mod;

import codeChallenge.excercise.exam1.authentication.User;

public class AuthenticationTypeOne implements Authenticator{
    @Override
    public Integer getType() {
        return 1;
    }

    @Override
    public void authenticate(User user) {
        //...
    }
}
