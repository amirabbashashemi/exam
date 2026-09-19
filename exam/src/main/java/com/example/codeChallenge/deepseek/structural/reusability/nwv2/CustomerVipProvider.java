package com.example.codeChallenge.deepseek.structural.reusability.nwv2;

@FunctionalInterface
public interface CustomerVipProvider {
    boolean isVip(String customerId);
}