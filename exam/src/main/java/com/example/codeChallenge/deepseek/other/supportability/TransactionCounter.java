package com.example.codeChallenge.deepseek.other.supportability;

// این کلاس در اختیار شماست (شبیه‌سازی سرویس اصلی)
class TransactionCounter {
    private long count = 0;
    public void increment() { count++; }
    public long getCount() { return count; }
}