package com.example.codeChallenge.deepseek.other.recoverability;

import java.io.IOException;
import java.util.List;
/*
شما مسئول پیاده‌سازی یک پردازندهٔ دسته‌ای (Batch Processor) برای تایید تراکنش‌های مالی هستید.
این پردازنده باید بتواند پس از هر نوع خاموشی ناگهانی (مثل OutOfMemoryError یا قطع برق) مجدداً اجرا شود و
 از جایی که کار را رها کرده، ادامه دهد،
 بدون اینکه هیچ تراکنشی دو بار پردازش شود یا از قلم بیفتد.

فرض کن متد process(Transaction t) از قبل در اختیار توست و فقط باید آن را صدا بزنی.
 */
public class RecoverableBatchProcessorOld {

    // سازنده: مسیر فایل وضعیت (state file) را دریافت می‌کند.
    public RecoverableBatchProcessorOld(String stateFilePath) {
    }

    // متد اصلی: لیست تراکنش‌ها را دریافت کرده و همه را پردازش می‌کند.
    // اما اگر قبلاً بخشی از آنها پردازش شده، از آخرین نقطه‌ی ذخیره ادامه می‌دهد.
    public void processAll(List<Transaction> transactions) throws IOException {
    }

}