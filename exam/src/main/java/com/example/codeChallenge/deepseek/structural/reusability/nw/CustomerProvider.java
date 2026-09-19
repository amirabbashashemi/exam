package com.example.codeChallenge.deepseek.structural.reusability.nw;

@FunctionalInterface
public interface CustomerProvider {
    boolean isVIP(String customerId);
}
