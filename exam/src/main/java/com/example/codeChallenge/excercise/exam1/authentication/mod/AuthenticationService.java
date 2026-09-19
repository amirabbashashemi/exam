package com.example.codeChallenge.excercise.exam1.authentication.mod;

import codeChallenge.excercise.exam1.authentication.User;

import java.util.Set;

public class AuthenticationService {
    private static Set<Authenticator> authenticators;

    public void register(Authenticator authenticator) {
        authenticators.add(authenticator);
    }

    public void authenticate(User user){
        Authenticator authenticator = authenticators.stream()
                .filter(item -> item.getType().equals(user.getType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Authentication not defined for type %d", user.getType())));

        authenticator.authenticate(user);
    }

}
