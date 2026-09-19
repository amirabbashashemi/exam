package com.example.codeChallenge.chatgpt.bArchitectureQualityAttributes.maintainability.strategy;

import codeChallenge.chatgpt.eCommon.User;

import java.util.LinkedList;
import java.util.List;

public class ValidatorContext {
    private final List<Validator> validators = new LinkedList<>();

    public final void registerValidators(List<Validator> inputValidators) {
        validators.addAll(inputValidators);
    }

    public void validate(User user) {
        validators.forEach(validator -> {
            validator.validate(user);
        });
    }

}
/*
Exception عمومی: هنوز RuntimeException است، بهتر است Exception اختصاصی (مثلاً InvalidUserException) استفاده شود تا مدیریت خطا راحت‌تر باشد.

Collection mutable: ValidatorContext هنوز لیست mutable دارد و می‌توان registerValidators را چند بار صدا زد → بهتر است یا immutable باشد یا پس از ثبت فقط خواندنی شود.

Order کنترل نشده: اگر ترتیب validateها مهم باشد، LinkedList خوب است ولی بهتر است explicit order یا Enum-based ordering داشته باشد.

تمام Validationها اجرا نمی‌شوند: الان اولین خطا باعث توقف می‌شود → اگر بخواهیم همه خطاها را جمع‌آوری کنیم، نیاز به تغییر دارد.
 */