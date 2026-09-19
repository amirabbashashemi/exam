package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.auditing.governance.neww;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    private final Map<Long, User> usersMap = new ConcurrentHashMap<>();
    private final Map<Long, List<UserHistory>> usersHistoryMap = new ConcurrentHashMap<>();

    public void updateUserName(Long userId, String newName, Long changerUserId) {
        User user = usersMap.get(userId);

        if (user != null) {
            saveHistory(userId, user, changerUserId);
            user.setName(newName);
        }
    }

    public void updateUserEmail(Long userId, String newEmail, Long changerUserId) {
        User user = usersMap.get(userId);
        if (user != null) {
            saveHistory(userId, user, changerUserId);
            user.setEmail(newEmail);
        }
    }

    private synchronized void saveHistory(Long userId, User user, Long changerUserId) {
        if (Objects.isNull(user)) {
            throw new RuntimeException(userId + " is not exists");
        }
        if (Objects.isNull(usersHistoryMap)) {
            throw new RuntimeException("users history is null");
        }

        List<UserHistory> userHistories = usersHistoryMap.get(userId);

        if (Objects.isNull(userHistories)) {
            userHistories = new LinkedList<>();
        }

        int lastVersion = !Objects.isNull(userHistories.getLast())
                ? userHistories.getLast().getVersion()
                : 0;

        UserHistory userHistory = new UserHistory();
        userHistory.setVersion(lastVersion + 1);
        userHistory.setId(user.getId());
        userHistory.setName(user.getName());
        userHistory.setEmail(user.getEmail());
        userHistory.setChangerUser(changerUserId);
        userHistory.setTimestamp(System.currentTimeMillis());

        userHistories.addLast(userHistory);

        usersHistoryMap.put(userId, userHistories);
    }
}