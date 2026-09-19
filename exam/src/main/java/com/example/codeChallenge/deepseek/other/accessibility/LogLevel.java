package com.example.codeChallenge.deepseek.other.accessibility;

public enum LogLevel {
    ERROR(0),
    INFO(1),
    DEBUG(2),
    ;

    int index;

    LogLevel(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
