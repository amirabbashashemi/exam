package com.example.codeChallenge.deepseek.architectureQualityAttributes.extensibility.payment.strategy.validation;

import ir.dotin.extensibility.payment.PaymentRequest;

import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class ValidatorService {
    private static final Map<ValidationEnum, Validator> ENUM_VALIDATOR_MAP = new ConcurrentHashMap<>();

    public ValidatorService() {
        initiate();
    }

    private void initiate() {
        ServiceLoader<Validator> serviceLoaders = ServiceLoader.load(Validator.class);

        serviceLoaders.forEach(validator -> {
            ENUM_VALIDATOR_MAP.putIfAbsent(validator.getType(), validator);
        });
    }

    public void validate(PaymentRequest paymentRequest, ValidationEnum validationEnum) {
        Validator validator = getValidator(validationEnum);
        validator.validate(paymentRequest);
    }

    private Validator getValidator(ValidationEnum validationEnum) {
        Validator validator = ENUM_VALIDATOR_MAP.get(validationEnum);

        if (Objects.isNull(validator)) {
            throw new IllegalArgumentException("Unknown paymentRequest type: " + validationEnum.name());
        }

        return validator;
    }
}
