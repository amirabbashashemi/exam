package com.example.codeChallenge.chatgpt.dOperationalAttributes.observability;

import java.util.ArrayList;
import java.util.List;

public class OperationServiceOld {
    private final List<String> logs = new ArrayList<>();

    public void execute(String action) {
        long start = System.currentTimeMillis();

        try {
            doBusiness(action);
            logs.add(action + " SUCCESS " + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            logs.add(action + " FAILED");
        }
    }

    private void doBusiness(String action) {
        if (action.equals("FAIL")) {
            throw new RuntimeException();
        }
    }

    public List<String> getLogs() {
        return logs;
    }
}
/*
ثبت زمان شروع و پایان عملیات
ثبت موفق یا ناموفق بودن عملیات
جدا بودن منطق Observability از منطق بیزینس
 */