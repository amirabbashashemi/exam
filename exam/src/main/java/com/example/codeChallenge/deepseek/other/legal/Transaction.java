package com.example.codeChallenge.deepseek.other.legal;

// رکورد تراکنش شامل اطلاعات حساس
record Transaction(String id, String cardNumber, String cvv, double amount) {}