package com.example.codeChallenge.deepseek.architectureQualityAttributes.extensibility.order.strategy.validation;

import ir.dotin.extensibility.order.Order;

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

    public void validate(Order order, ValidationEnum validationEnum) {
        Validator validator = getValidator(validationEnum);
        validator.validate(order);
    }

    private Validator getValidator(ValidationEnum validationEnum) {
        Validator validator = ENUM_VALIDATOR_MAP.get(validationEnum);

        if (Objects.isNull(validator)) {
            throw new IllegalArgumentException("Unknown order type: " + validationEnum.name());
        }

        return validator;
    }
}
