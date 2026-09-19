package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.interoperability.neww.strategy.connection;

import java.util.Map;
import java.util.Objects;

public class UserSenderContext {
    private final Map<SenderType, UserSender> senderTypeUserSenderMap;

    public UserSenderContext(Map<SenderType, UserSender> senderTypeUserSenderMap) {
        this.senderTypeUserSenderMap = senderTypeUserSenderMap;
    }

    public void send(SenderType senderType, String message) {
        UserSender userSender = this.senderTypeUserSenderMap.get(senderType);

        if (Objects.nonNull(userSender)) {
            userSender.send(message);
        } else {
            throw new RuntimeException("userSender is not valid");
        }
    }

}
